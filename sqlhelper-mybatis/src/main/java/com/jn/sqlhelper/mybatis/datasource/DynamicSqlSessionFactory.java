/*
 * Copyright 2020 the original author or authors.
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

package com.jn.sqlhelper.mybatis.datasource;

import com.jn.langx.util.collection.Collects;
import com.jn.sqlhelper.datasource.key.DataSourceKey;
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
        return null;
    }

    @Override
    public SqlSession openSession(boolean autoCommit) {
        return null;
    }

    @Override
    public SqlSession openSession(Connection connection) {
        return null;
    }

    @Override
    public SqlSession openSession(TransactionIsolationLevel level) {
        return null;
    }

    @Override
    public SqlSession openSession(ExecutorType execType) {
        return null;
    }

    @Override
    public SqlSession openSession(ExecutorType execType, boolean autoCommit) {
        return null;
    }

    @Override
    public SqlSession openSession(ExecutorType execType, TransactionIsolationLevel level) {
        return null;
    }

    @Override
    public SqlSession openSession(ExecutorType execType, Connection connection) {
        return null;
    }

    @Override
    public Configuration getConfiguration() {
        if (size() == 1) {
            return Collects.findFirst(factoryMap.values()).getConfiguration();
        }
        DataSourceKey key = null;
        return factoryMap.get(key).getConfiguration();
    }

    public Map<DataSourceKey, SqlSessionFactory> getDelegates() {
        return Collects.newHashMap(factoryMap);
    }

}
