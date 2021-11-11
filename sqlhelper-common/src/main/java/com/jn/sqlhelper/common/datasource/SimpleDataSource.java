/*
 * Copyright 2019 the original author or authors.
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

package com.jn.sqlhelper.common.datasource;

import com.jn.langx.text.StringTemplates;
import com.jn.sqlhelper.common.connection.ConnectionConfiguration;
import com.jn.sqlhelper.common.connection.ConnectionFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class SimpleDataSource implements DataSource {
    /**
     * seconds
     */
    private int loginTimeout = 0;
    private ConnectionFactory connectionFactory;

    @Override
    public Connection getConnection() throws SQLException {
        return connectionFactory.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        ConnectionConfiguration configuration = null;
        try {
            configuration = (ConnectionConfiguration) connectionFactory.getConnectionConfiguration().clone();
            return new ConnectionFactory(configuration).getConnection();
        } catch (CloneNotSupportedException ex) {
            return getConnection();
        }
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException(StringTemplates.formatWithPlaceholder("Class {} is not a wrapper", SimpleDataSource.class));
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return DriverManager.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        DriverManager.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return loginTimeout;
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }
}
