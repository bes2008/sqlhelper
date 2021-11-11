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

package com.jn.sqlhelper.common.connection;

import com.jn.langx.util.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {
    private static Logger logger = LoggerFactory.getLogger(ConnectionFactory.class);
    private ConnectionConfiguration connectionConfiguration;

    public ConnectionFactory(String driver, String url, String user, String password, Properties driverProps) {
        this(new ConnectionConfiguration(driver, url, user, password, driverProps));
    }

    public ConnectionFactory(ConnectionConfiguration connConfig) {
        this.connectionConfiguration = connConfig;
        if (connConfig.getDriver() != null) {
            try {
                Class.forName(connConfig.getDriver(), false, Thread.currentThread().getContextClassLoader());
            } catch (ClassNotFoundException e) {
                throw Throwables.wrapAsRuntimeException(e);
            }
        }
    }

    public <T extends ConnectionConfiguration> void setConnectionConfiguration(T connectionConfiguration) {
        this.connectionConfiguration = connectionConfiguration;
    }

    public Connection getConnection() {
        try {
            if (connectionConfiguration.getDriver() != null) {
                try {
                    Class.forName(connectionConfiguration.getDriver(), true, Thread.currentThread().getContextClassLoader());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            if (connectionConfiguration.getDriverProps() != null && !connectionConfiguration.getDriverProps().isEmpty()) {
                return DriverManager.getConnection(connectionConfiguration.getUrl(), connectionConfiguration.getDriverProps());
            }
            return DriverManager.getConnection(connectionConfiguration.getUrl(), connectionConfiguration.getUser(), connectionConfiguration.getPassword());
        } catch (SQLException e) {
            logger.warn("Error occur when get connection with configuration: {}, error message : {}, stack:", connectionConfiguration.toString(), e.getMessage(), e);
        }
        return null;
    }


    public ConnectionConfiguration getConnectionConfiguration() {
        return connectionConfiguration;
    }
}
