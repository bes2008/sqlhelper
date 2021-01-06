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

package com.jn.sqlhelper.mybatis.spring.datasource;

import com.jn.langx.Delegatable;
import org.apache.ibatis.session.*;
import org.springframework.dao.support.PersistenceExceptionTranslator;

import java.sql.Connection;

public class DelegatingSqlSessionFactory implements SqlSessionFactory, Delegatable<SqlSessionFactory> {
    private SqlSessionFactory delegate;
    private PersistenceExceptionTranslator persistenceExceptionTranslator;

    @Override
    public SqlSession openSession() {
        return delegate.openSession();
    }

    @Override
    public SqlSession openSession(boolean autoCommit) {
        return delegate.openSession(autoCommit);
    }

    @Override
    public SqlSession openSession(Connection connection) {
        return delegate.openSession(connection);
    }

    @Override
    public SqlSession openSession(TransactionIsolationLevel level) {
        return delegate.openSession(level);
    }

    @Override
    public SqlSession openSession(ExecutorType execType) {
        return delegate.openSession(execType);
    }

    @Override
    public SqlSession openSession(ExecutorType execType, boolean autoCommit) {
        return delegate.openSession(execType, autoCommit);
    }

    @Override
    public SqlSession openSession(ExecutorType execType, TransactionIsolationLevel level) {
        return delegate.openSession(execType, level);
    }

    @Override
    public SqlSession openSession(ExecutorType execType, Connection connection) {
        return delegate.openSession(execType, connection);
    }

    @Override
    public Configuration getConfiguration() {
        return delegate.getConfiguration();
    }

    @Override
    public SqlSessionFactory getDelegate() {
        return delegate;
    }

    @Override
    public void setDelegate(SqlSessionFactory delegate) {
        this.delegate = delegate;
    }

    public PersistenceExceptionTranslator getPersistenceExceptionTranslator() {
        return persistenceExceptionTranslator;
    }

    public void setPersistenceExceptionTranslator(PersistenceExceptionTranslator persistenceExceptionTranslator) {
        this.persistenceExceptionTranslator = persistenceExceptionTranslator;
    }
}
