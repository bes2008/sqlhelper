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

package com.jn.sqlhelper.mybatis.session.transaction;

import com.jn.sqlhelper.common.transaction.TransactionalResource;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;

public class SqlSessionTransactionalResource implements TransactionalResource {
    private String name;
    private SqlSession session;
    private SqlSessionFactory sessionFactory;

    public SqlSessionTransactionalResource(String name, SqlSession session, SqlSessionFactory sessionFactory) {
        this.name = name;
        this.session = session;
        this.sessionFactory = sessionFactory;
    }

    public SqlSession getSession() {
        return session;
    }

    public SqlSessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    public void commit(boolean force) throws SQLException {
        session.commit(force);
    }

    @Override
    public void rollback() throws SQLException {
        session.rollback();
    }

    @Override
    public void setName(String s) {
        if (s != null) {
            this.name = s;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isClosed() {
        return session == null;
    }

    @Override
    public void close() throws SQLException {
        if(session!=null){
            session.close();
        }
    }
}
