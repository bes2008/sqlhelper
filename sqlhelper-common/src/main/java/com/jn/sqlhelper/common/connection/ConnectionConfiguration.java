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

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConnectionConfiguration implements Cloneable {
    public static final String URL = "jdbc.url";
    public static final String USER = "jdbc.user";
    public static final String PASSWORD = "jdbc.password";
    public static final String DRIVER = "jdbc.driver";

    private String url;
    private String user;
    private String password;
    private String driver;
    private Properties driverProps = new Properties();

    public ConnectionConfiguration() {
    }

    public ConnectionConfiguration(String driver, String url, String user, String password, Properties driverProps) {
        if (driverProps != null) {
            setDriverProps(driverProps);
        }
        setDriver(driver);
        setUrl(url);
        setUser(user);
        setPassword(password);
    }

    public static ConnectionConfiguration loadConfig(InputStream input) throws IOException {
        Properties props = new Properties();
        props.load(input);
        ConnectionConfiguration config = new ConnectionConfiguration();
        config.setDriverProps(props);

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


        return config;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        ConnectionConfiguration conn = new ConnectionConfiguration();


        final Properties props = new Properties();
        Collects.forEach(this.driverProps, new Consumer2<Object, Object>() {
            @Override
            public void accept(Object key, Object value) {
                props.setProperty(key.toString(), value.toString());
            }
        });

        conn.setDriverProps(props);

        conn.setDriver(driver);
        conn.setUrl(url);
        conn.setUser(user);
        conn.setPassword(password);
        return conn;
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
        driverProps.setProperty(USER, user);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        driverProps.setProperty(PASSWORD, password);
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
