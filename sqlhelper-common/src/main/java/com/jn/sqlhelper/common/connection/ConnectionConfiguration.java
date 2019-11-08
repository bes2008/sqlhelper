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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConnectionConfiguration{
    public static final String URL = "jdbc.url";
    public static final String USER = "jdbc.user";
    public static final String PASSWORD = "jdbc.password";
    public static final String DRIVER = "jdbc.driver";

    private String url;
    private String user;
    private String password;
    private String driver;
    private Properties driverProps;

    public ConnectionConfiguration() {
    }

    public ConnectionConfiguration(String driver, String url, String user, String password, Properties driverProps) {
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.password = password;
        this.driverProps = driverProps;
    }

    public static ConnectionConfiguration loadConfig(InputStream input) throws IOException {
        Properties props = new Properties();
        props.load(input);
        ConnectionConfiguration config = new ConnectionConfiguration();
        config.setUrl(props.getProperty(URL));
        config.setDriver(props.getProperty(DRIVER));

        String user = props.getProperty(USER);
        String password = props.getProperty(PASSWORD);

        config.setUser(user);
        config.setPassword(password);

        props.setProperty("user", user);
        props.setProperty("password", password);

        props.remove(URL);
        props.remove(DRIVER);
        props.remove(PASSWORD);
        props.remove(USER);

        config.setDriverProps(props);
        return config;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public Properties getDriverProps() {
        return driverProps;
    }

    public void setDriverProps(Properties driverProps) {
        this.driverProps = driverProps;
    }
}
