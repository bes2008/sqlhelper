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
import com.jn.sqlhelper.datasource.key.DataSourceKey;
import com.jn.sqlhelper.datasource.key.MethodInvocationDataSourceKeySelector;
import org.apache.ibatis.session.*;

import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicSqlSessionFactory implements SqlSessionFactory {

    private ConcurrentHashMap<DataSourceKey, SqlSessionFactory> factoryMap = new ConcurrentHashMap<DataSourceKey, SqlSessionFactory>();

    public void addSqlSessionFactory(DataSourceKey key, SqlSessionFactory sessionFactory) {
        factoryMap.putIfAbsent(key, sessionFactory);
    }

    public int size() {
        return factoryMap.size();
    }

    @Override
    public SqlSession openSession() {
        return getDelegatingSqlSessionFactory().openSession();
    }

    @Override
    public SqlSession openSession(boolean autoCommit) {
        return getDelegatingSqlSessionFactory().openSession(autoCommit);
    }

    @Override
    public SqlSession openSession(Connection connection) {
        return getDelegatingSqlSessionFactory().openSession(connection);
    }

    @Override
    public SqlSession openSession(TransactionIsolationLevel level) {
        return getDelegatingSqlSessionFactory().openSession(level);
    }

    @Override
    public SqlSession openSession(ExecutorType execType) {
        return getDelegatingSqlSessionFactory().openSession(execType);
    }

    @Override
    public SqlSession openSession(ExecutorType execType, boolean autoCommit) {
        return getDelegatingSqlSessionFactory().openSession(execType, autoCommit);
    }

    @Override
    public SqlSession openSession(ExecutorType execType, TransactionIsolationLevel level) {
        return getDelegatingSqlSessionFactory().openSession(execType, level);
    }

    @Override
    public SqlSession openSession(ExecutorType execType, Connection connection) {
        return getDelegatingSqlSessionFactory().openSession(execType, connection);
    }

    @Override
    public Configuration getConfiguration() {
        SqlSessionFactory delegate = getDelegatingSqlSessionFactory();
        if (delegate == null) {
            // 启动阶段，会走这里
            return Collects.findFirst(factoryMap.values()).getConfiguration();
        } else {
            return getDelegatingSqlSessionFactory().getConfiguration();
        }
    }

    public Map<DataSourceKey, SqlSessionFactory> getDelegates() {
        return Collects.newHashMap(factoryMap);
    }


    private SqlSessionFactory getDelegatingSqlSessionFactory() {
        if (MethodInvocationDataSourceKeySelector.getCurrent() != null) {
            return factoryMap.get(MethodInvocationDataSourceKeySelector.getCurrent());
        }
        return null;
    }
}
