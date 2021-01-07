/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-2.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jn.sqlhelper.mybatis.spring.session.factory.dynamicdatasource;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.reflect.Reflects;
import com.jn.sqlhelper.common.transaction.Transaction;
import com.jn.sqlhelper.common.transaction.utils.TransactionThreadContext;
import com.jn.sqlhelper.datasource.key.DataSourceKey;
import com.jn.sqlhelper.datasource.key.MethodInvocationDataSourceKeySelector;
import com.jn.sqlhelper.mybatis.session.transaction.SqlSessionTransactionalResource;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.*;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.dao.support.PersistenceExceptionTranslator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import static java.lang.reflect.Proxy.newProxyInstance;
import static org.apache.ibatis.reflection.ExceptionUtil.unwrapThrowable;
import static org.mybatis.spring.SqlSessionUtils.*;


public class DynamicSqlSessionTemplate extends SqlSessionTemplate {
    private MethodInvocationDataSourceKeySelector selector;
    private SqlSession sessionProxy;

    public DynamicSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        this(sqlSessionFactory, null);
    }

    /**
     * @param sqlSessionFactory @see DynamicSqlSessionFactory
     * @param executorType
     */
    public DynamicSqlSessionTemplate(SqlSessionFactory sqlSessionFactory, ExecutorType executorType) {
        super(sqlSessionFactory, ExecutorType.SIMPLE, null);
        this.sessionProxy = (SqlSession) newProxyInstance(
                SqlSessionFactory.class.getClassLoader(),
                new Class[]{SqlSession.class},
                new SqlSessionTransactionInterceptor());
    }

    public void setSelector(MethodInvocationDataSourceKeySelector selector) {
        this.selector = selector;
    }

    private DynamicSqlSessionFactory getDynamicSqlSessionFactory() {
        return (DynamicSqlSessionFactory) this.getSqlSessionFactory();
    }


    /**
     * {@inheritDoc}
     * 在初始化阶段，初始化 各种Mapper时调用该方法
     */
    @Override
    public <T> T getMapper(final Class<T> mapperInterface) {
        DynamicSqlSessionFactory sessionFactory = getDynamicSqlSessionFactory();
        final Map<DataSourceKey, Object> delegateMapperMap = Collects.emptyHashMap();
        Collects.forEach(sessionFactory.getDelegates(), new Consumer2<DataSourceKey, SqlSessionFactory>() {
            @Override
            public void accept(DataSourceKey key, SqlSessionFactory delegateFactory) {
                Object mybatisMapperProxy = delegateFactory.getConfiguration().getMapper(mapperInterface, DynamicSqlSessionTemplate.this);
                delegateMapperMap.put(key, mybatisMapperProxy);
            }
        });
        DynamicMapper mapper = new DynamicMapper(mapperInterface, delegateMapperMap, selector);
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface}, mapper);
    }

    @Override
    public SqlSessionFactory getSqlSessionFactory() {
        return super.getSqlSessionFactory();
    }

    public SqlSessionFactory getLocalSqlSessionFactory() {
        DynamicSqlSessionFactory sessionFactory = getDynamicSqlSessionFactory();
        if (sessionFactory.size() == 1) {
            return sessionFactory;
        }
        return getSqlSessionFactory();
    }

    /**
     * {@inheritDoc}
     * 如果只有一个数据源，则直接就是原始Configuration
     * 如果有多个，并且未指定取哪个，则取 primary 数据源的
     * 如果有多个，并且指定取了哪个，则取 指定的数据源的
     */
    @Override
    public Configuration getConfiguration() {
        DynamicSqlSessionFactory sessionFactory = getDynamicSqlSessionFactory();
        if (sessionFactory.size() == 1) {
            return sessionFactory.getConfiguration();
        }
        return this.getLocalSqlSessionFactory().getConfiguration();
    }


    private class SqlSessionTransactionInterceptor implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            SqlSessionFactory sqlSessionFactory = DynamicSqlSessionTemplate.this.getSqlSessionFactory();
            ExecutorType executorType = DynamicSqlSessionTemplate.this.getExecutorType();
            PersistenceExceptionTranslator exceptionTranslator = DynamicSqlSessionTemplate.this.getPersistenceExceptionTranslator();
            // 这个是实际的 sqlSession
            SqlSession sqlSession = getSqlSession(sqlSessionFactory, executorType, exceptionTranslator);

            DataSourceKey key = MethodInvocationDataSourceKeySelector.getCurrent();
            Transaction transaction = TransactionThreadContext.get();
            // 当改调用发生在sqlhelper transaction manager 范围内时，需要注册
            if (key != null && transaction != null) {
                TransactionThreadContext.bindTransactionResource(key, new SqlSessionTransactionalResource(Reflects.getMethodString(method), sqlSession, sqlSessionFactory));
            }
            // 判断 sqlSession 是否使用了事务管理
            boolean isSqlSessionTransactional = false;
            try {
                Object result = method.invoke(sqlSession, args);

                // 判断 sqlSession 是否使用了Spring 事务管理
                isSqlSessionTransactional = isSqlSessionTransactional(sqlSession, sqlSessionFactory);
                if (!isSqlSessionTransactional) {
                    // 判断 sqlSession 是否使用了 SqlHelper DynamicDataSource 事务管理

                    if (key != null) {

                        if (transaction != null) {
                            isSqlSessionTransactional = transaction.hasResource(key);
                        }
                    }
                }
                if (!isSqlSessionTransactional) {
                    sqlSession.commit(true);
                }
                return result;
            } catch (Throwable t) {
                Throwable unwrapped = unwrapThrowable(t);
                if (exceptionTranslator != null && unwrapped instanceof PersistenceException) {
                    // release the connection to avoid a deadlock if the translator is no loaded. See issue #22
                    closeSqlSession(sqlSession, sqlSessionFactory);
                    sqlSession = null;
                    Throwable translated = exceptionTranslator.translateExceptionIfPossible((PersistenceException) unwrapped);
                    if (translated != null) {
                        unwrapped = translated;
                    }
                }
                throw unwrapped;
            } finally {
                if (sqlSession != null && !isSqlSessionTransactional) {
                    closeSqlSession(sqlSession, sqlSessionFactory);
                }
            }
        }
    }


    @Override
    public <T> T selectOne(String statement) {
        return sessionProxy.selectOne(statement);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        return sessionProxy.selectOne(statement, parameter);
    }

    @Override
    public <E> List<E> selectList(String statement) {
        return sessionProxy.selectList(statement);
    }

    @Override
    public <E> List<E> selectList(String statement, Object parameter) {
        return sessionProxy.selectList(statement, parameter);
    }

    @Override
    public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
        return sessionProxy.selectList(statement, parameter, rowBounds);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
        return sessionProxy.selectMap(statement, mapKey);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey) {
        return sessionProxy.selectMap(statement, parameter, mapKey);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey, RowBounds rowBounds) {
        return sessionProxy.selectMap(statement, parameter, mapKey, rowBounds);
    }

    @Override
    public <T> Cursor<T> selectCursor(String statement) {
        return sessionProxy.selectCursor(statement);
    }

    @Override
    public <T> Cursor<T> selectCursor(String statement, Object parameter) {
        return sessionProxy.selectCursor(statement, parameter);
    }

    @Override
    public <T> Cursor<T> selectCursor(String statement, Object parameter, RowBounds rowBounds) {
        return sessionProxy.selectCursor(statement, parameter, rowBounds);
    }

    @Override
    public void select(String statement, Object parameter, ResultHandler handler) {
        sessionProxy.select(statement, parameter, handler);
    }

    @Override
    public void select(String statement, ResultHandler handler) {
        sessionProxy.select(statement, handler);
    }

    @Override
    public void select(String statement, Object parameter, RowBounds rowBounds, ResultHandler handler) {
        sessionProxy.select(statement, parameter, rowBounds, handler);
    }

    @Override
    public int insert(String statement) {
        return sessionProxy.insert(statement);
    }

    @Override
    public int insert(String statement, Object parameter) {
        return sessionProxy.insert(statement, parameter);
    }

    @Override
    public int update(String statement) {
        return sessionProxy.update(statement);
    }

    @Override
    public int update(String statement, Object parameter) {
        return sessionProxy.update(statement, parameter);
    }

    @Override
    public int delete(String statement) {
        return sessionProxy.delete(statement);
    }

    @Override
    public int delete(String statement, Object parameter) {
        return sessionProxy.delete(statement, parameter);
    }

    @Override
    public void commit() {
        throw new UnsupportedOperationException("Manual rollback is not allowed over a container (Spring or SQLHelper Dynamic DataSource) managed SqlSession");
    }

    @Override
    public void commit(boolean force) {
        throw new UnsupportedOperationException("Manual commit is not allowed over a container (Spring or SQLHelper Dynamic DataSource) managed SqlSession");
    }

    @Override
    public void rollback() {
        throw new UnsupportedOperationException("Manual rollback is not allowed over a container (Spring or SQLHelper Dynamic DataSource) managed SqlSession");
    }

    @Override
    public void rollback(boolean force) {
        throw new UnsupportedOperationException("Manual rollback is not allowed over a container (Spring or SQLHelper Dynamic DataSource) managed SqlSession");
    }

    @Override
    public List<BatchResult> flushStatements() {
        return sessionProxy.flushStatements();
    }

    @Override
    public void close() {
        sessionProxy.close();
    }

    @Override
    public void clearCache() {
        sessionProxy.clearCache();
    }

    @Override
    public Connection getConnection() {
        return sessionProxy.getConnection();
    }
}
