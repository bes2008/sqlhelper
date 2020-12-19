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

package com.jn.sqlhelper.datasource;

import com.jn.langx.Delegatable;
import com.jn.langx.Named;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.sqlhelper.datasource.key.DataSourceKey;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Logger;

public class DelegatingNamedDataSource implements NamedDataSource, Delegatable<DataSource>, Initializable {
    private final DataSourceKey dataSourceKey = new DataSourceKey(DataSources.DATASOURCE_GROUP, "undefined");
    private DataSource delegate;

    @Override
    public DataSource getDelegate() {
        return delegate;
    }

    @Override
    public void setDelegate(DataSource delegate) {
        Preconditions.checkNotNull(delegate, "'delegate' must not be null");
        this.delegate = delegate;
    }


    /**
     * Create a new DelegatingDataSource.
     */
    public DelegatingNamedDataSource() {
    }

    /**
     * Create a new DelegatingDataSource.
     * @param targetDataSource the target DataSource
     */
    public DelegatingNamedDataSource(DataSource targetDataSource) {
        setDelegate(targetDataSource);
    }


    public void init() throws InitializationException{
        if (getDelegate() == null) {
            throw new InitializationException("Property 'targetDataSource' is required");
        }
    }


    @Override
    public Connection getConnection() throws SQLException {
        return getDelegate().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getDelegate().getConnection(username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return getDelegate().getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        getDelegate().setLogWriter(out);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return getDelegate().getLoginTimeout();
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        getDelegate().setLoginTimeout(seconds);
    }


    //---------------------------------------------------------------------
    // Implementation of JDBC 4.0's Wrapper interface
    //---------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return (T) this;
        }
        return getDelegate().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return (iface.isInstance(this) || getDelegate().isWrapperFor(iface));
    }


    //---------------------------------------------------------------------
    // Implementation of JDBC 4.1's getParentLogger method
    //---------------------------------------------------------------------

    public Logger getParentLogger() {
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }


    public String getGroup() {
        return dataSourceKey.getGroup();
    }

    public void setGroup(String group) {
        this.dataSourceKey.setGroup(group);
    }

    @Override
    public void setName(String name) {
        this.dataSourceKey.setName(name);
    }

    @Override
    public String getName() {
        return this.dataSourceKey.getName();
    }

    @Override
    public DataSourceKey getDataSourceKey() {
        return this.dataSourceKey;
    }
}
