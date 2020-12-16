/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the LGPL, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jn.sqlhelper.mybatis.spring;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.MyBatisExceptionTranslator;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.dao.support.PersistenceExceptionTranslator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.List;


public class DynamicSqlSessionTemplate extends SqlSessionTemplate {
    private final SqlSession sessionProxy;

    public DynamicSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        this(sqlSessionFactory, sqlSessionFactory.getConfiguration().getDefaultExecutorType());
    }

    /**
     * Constructs a Spring managed SqlSession with the {@code SqlSessionFactory}
     * provided as an argument and the given {@code ExecutorType}
     * {@code ExecutorType} cannot be changed once the {@code SqlSessionTemplate}
     * is constructed.
     *
     */
    public DynamicSqlSessionTemplate(SqlSessionFactory sqlSessionFactory, ExecutorType executorType) {
        this(sqlSessionFactory, executorType,new MyBatisExceptionTranslator(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(), true));
    }

    public DynamicSqlSessionTemplate(SqlSessionFactory sqlSessionFactory, ExecutorType executorType, PersistenceExceptionTranslator exceptionTranslator) {
        super(sqlSessionFactory, executorType, exceptionTranslator);
        this.sessionProxy = (SqlSession) Proxy.newProxyInstance(
                SqlSessionFactory.class.getClassLoader(),
                new Class[]{SqlSession.class},
                new MultiDataSourceSqlSessionInterceptor());
    }

    public void clearCache() {
        this.sessionProxy.clearCache();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int insert(String statement) {
        return this.sessionProxy.insert(statement);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int insert(String statement, Object parameter) {
        return this.sessionProxy.insert(statement, parameter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int update(String statement) {
        return this.sessionProxy.update(statement);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int update(String statement, Object parameter) {
        return this.sessionProxy.update(statement, parameter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int delete(String statement) {
        return this.sessionProxy.delete(statement);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int delete(String statement, Object parameter) {
        return this.sessionProxy.delete(statement, parameter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getMapper(Class<T> type) {
        return getConfiguration().getMapper(type, this);
    }

    @Override
    public SqlSessionFactory getSqlSessionFactory() {
        return super.getSqlSessionFactory();
    }

    public SqlSessionFactory getLocalSqlSessionFactory() {
        return getSqlSessionFactory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration getConfiguration() {
        return this.getLocalSqlSessionFactory().getConfiguration();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection() {
        return this.sessionProxy.getConnection();
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.2
     */
    @Override
    public List<BatchResult> flushStatements() {
        return this.sessionProxy.flushStatements();
    }

    /**
     * Allow gently dispose bean:
     * <pre>
     * {@code
     *
     * <bean id="sqlSession" class="org.mybatis.spring.SqlSessionTemplate">
     *  <constructor-arg index="0" ref="sqlSessionFactory" />
     * </bean>
     * }
     * </pre>
     * <p>
     * The implementation of {@link DisposableBean} forces spring context to use {@link DisposableBean#destroy()} method instead of {@link SqlSessionTemplate#close()} to shutdown gently.
     *
     * @see SqlSessionTemplate#close()
     */
    @Override
    public void destroy() throws Exception {
        //This method forces spring disposer to avoid call of SqlSessionTemplate.close() which gives UnsupportedOperationException
    }

    /**
     * Proxy needed to route MyBatis method calls to the proper SqlSession got
     * from Spring's Transaction Manager
     * It also unwraps exceptions thrown by {@code Method#invoke(Object, Object...)} to
     * pass a {@code PersistenceException} to the {@code PersistenceExceptionTranslator}.
     */
    private class MultiDataSourceSqlSessionInterceptor implements InvocationHandler {


        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            SqlSession sqlSession = SqlSessionUtils.getSqlSession(
                    DynamicSqlSessionTemplate.this.getSqlSessionFactory(),
                    DynamicSqlSessionTemplate.this.getExecutorType(),
                    DynamicSqlSessionTemplate.this.getPersistenceExceptionTranslator());
            try {
                Object result = method.invoke(sqlSession, args);
                if (!SqlSessionUtils.isSqlSessionTransactional(sqlSession, DynamicSqlSessionTemplate.this.getSqlSessionFactory())) {
                    // force commit even on non-dirty sessions because some databases require
                    // a commit/rollback before calling close()
                    sqlSession.commit(true);
                }
                return result;
            } catch (Throwable t) {
                Throwable unwrapped = ExceptionUtil.unwrapThrowable(t);
                if (DynamicSqlSessionTemplate.this.getPersistenceExceptionTranslator() != null && unwrapped instanceof PersistenceException) {
                    // release the connection to avoid a deadlock if the translator is no loaded. See issue #22
                    SqlSessionUtils.closeSqlSession(sqlSession, DynamicSqlSessionTemplate.this.getSqlSessionFactory());
                    sqlSession = null;
                    Throwable translated = DynamicSqlSessionTemplate.this.getPersistenceExceptionTranslator().translateExceptionIfPossible((PersistenceException) unwrapped);
                    if (translated != null) {
                        unwrapped = translated;
                    }
                }
                throw unwrapped;
            } finally {
                if (sqlSession != null) {
                    SqlSessionUtils.closeSqlSession(sqlSession, DynamicSqlSessionTemplate.this.getSqlSessionFactory());
                }
            }
        }
    }

}
