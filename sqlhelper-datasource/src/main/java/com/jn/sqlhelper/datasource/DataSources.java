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

import com.jn.langx.Named;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.NotEmpty;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.exception.IllegalPropertyException;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.security.SecurityException;
import com.jn.sqlhelper.common.security.DriverPropertiesCipherer;
import com.jn.sqlhelper.datasource.config.DataSourceProperties;
import com.jn.sqlhelper.datasource.key.DataSourceKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.UUID;

/**
 * @since 3.4.0
 */
public class DataSources {
    private static final Logger logger = LoggerFactory.getLogger(DataSources.class);


    public static final String DATASOURCE_IMPLEMENT_KEY_TOMCAT = "tomcat";
    public static final String DATASOURCE_IMPLEMENT_KEY_HIKARICP = "hikaricp";
    public static final String DATASOURCE_IMPLEMENT_KEY_DRUID = "druid";
    public static final String DATASOURCE_IMPLEMENT_KEY_DBCP2 = "dbcp2";
    public static final String DATASOURCE_IMPLEMENT_KEY_C3P0 = "c3p0";

    public static final String DATASOURCE_PROP_IMPLEMENTATION = "datasource.implementation";
    public static final String DATASOURCE_PROP_GROUP = "datasource.group";
    public static final String DATASOURCE_PROP_NAME = "datasource.name";

    public static final String DATASOURCE_PRIMARY_GROUP = "primary";
    public static final String DATASOURCE_PRIMARY_NAME = "primary";
    public static final DataSourceKey DATASOURCE_PRIMARY = new DataSourceKey(DATASOURCE_PRIMARY_GROUP, DATASOURCE_PRIMARY_NAME);
    public static final String DATASOURCE_NAME_WILDCARD = "*";

    /**
     * Close the given Connection, obtained from the given DataSource,
     * if it is not managed externally (that is, not bound to the thread).
     *
     * @param con        the Connection to close if necessary
     *                   (if this is {@code null}, the call will be ignored)
     * @param dataSource the DataSource that the Connection was obtained from
     *                   (may be {@code null})
     */
    public static void releaseConnection(Connection con, DataSource dataSource) {
        try {
            doReleaseConnection(con, dataSource);
        } catch (SQLException ex) {
            logger.debug("Could not close JDBC Connection", ex);
        } catch (Throwable ex) {
            logger.debug("Unexpected exception on closing JDBC Connection", ex);
        }
    }

    /**
     * Actually close the given Connection, obtained from the given DataSource.
     * Same as {@link #releaseConnection}, but throwing the original SQLException.
     *
     * @param con        the Connection to close if necessary
     *                   (if this is {@code null}, the call will be ignored)
     * @param dataSource the DataSource that the Connection was obtained from
     *                   (may be {@code null})
     * @throws SQLException if thrown by JDBC methods
     */
    public static void doReleaseConnection(Connection con, DataSource dataSource) throws SQLException {
        if (con == null) {
            return;
        }
        doCloseConnection(con, dataSource);
    }


    /**
     * Close the Connection, unless a {@link SmartDataSource} doesn't want us to.
     *
     * @param con        the Connection to close if necessary
     * @param dataSource the DataSource that the Connection was obtained from
     * @throws SQLException if thrown by JDBC methods
     * @see Connection#close()
     * @see SmartDataSource#shouldClose(Connection)
     */
    public static void doCloseConnection(Connection con, DataSource dataSource) throws SQLException {
        if (!(dataSource instanceof SmartDataSource) || ((SmartDataSource) dataSource).shouldClose(con)) {
            con.close();
        }
    }

    public static boolean isImplementationKeyMatched(@NonNull String expectedKey, DataSourceProperties properties) {
        Preconditions.checkNotNull(expectedKey, "the expected jdbc datasource implementation key is null or empty");
        String implementationKey = properties.getImplementation();
        boolean implementationKeyMatched = true;
        if (Emptys.isNotEmpty(implementationKey)) {
            if (!Objs.equals(expectedKey, implementationKey)) {
                implementationKeyMatched = false;
            }
        }
        return implementationKeyMatched;
    }


    private static final String DATASOURCE_ID_SEPARATOR = "SQLHelper.DynamicDataSource.ID.separator";

    public static String getDatasourceIdSeparator() {
        return System.getProperty(DATASOURCE_ID_SEPARATOR, "::");
    }


    public static NamedDataSource toNamedDataSource(DataSource dataSource) {
        if (dataSource instanceof NamedDataSource) {
            return (NamedDataSource) dataSource;
        }
        String name = null;
        if (dataSource instanceof Named) {
            name = ((Named) dataSource).getName();
        }
        if (Strings.isBlank(name)) {
            name = UUID.randomUUID().toString();
        }
        return toNamedDataSource(dataSource, name, null);
    }

    public static NamedDataSource toNamedDataSource(@NonNull DataSource delegate, String name, DataSourceProperties dataSourceProperties) {
        return toNamedDataSource(delegate, null, name, dataSourceProperties);
    }

    public static NamedDataSource toNamedDataSource(DataSource dataSource, DataSourceKey dataSourceKey, DataSourceProperties dataSourceProperties) {
        return toNamedDataSource(dataSource, dataSourceKey.getGroup(), dataSourceKey.getName(), dataSourceProperties);
    }

    public static NamedDataSource toNamedDataSource(@NonNull DataSource delegate, @Nullable String group, @NonNull String name, DataSourceProperties dataSourceProperties) {
        Preconditions.checkNotNull(delegate, "the delegate is null");
        Preconditions.checkNotEmpty(name, "the name is null or empty");
        group = Strings.useValueIfBlank(group, DataSources.DATASOURCE_PRIMARY_GROUP);

        if (delegate instanceof NamedDataSource) {
            NamedDataSource namedDataSource = (NamedDataSource) delegate;
            namedDataSource.setGroup(group);
            namedDataSource.setName(name);
            return namedDataSource;
        }

        DelegatingNamedDataSource dataSource = new DelegatingNamedDataSource();
        dataSource.setDelegate(delegate);
        dataSource.setName(name);
        dataSource.setGroup(group);
        dataSource.setDataSourceProperties(dataSourceProperties);
        return dataSource;
    }

    public static DataSourceKey buildDataSourceKey(String idString) {
        String separator = DataSources.getDatasourceIdSeparator();
        if (!Strings.contains(idString, separator)) {
            throw new IllegalArgumentException(StringTemplates.formatWithPlaceholder("Illegal datasource id: {}", "/"));
        }

        int index = idString.indexOf(separator);
        if (index > 0) {
            String group = idString.substring(0, index);
            String name = idString.substring(index + separator.length());
            if (Emptys.isNoneEmpty(group, name)) {
                return new DataSourceKey(group, name);
            }
            throw new IllegalPropertyException("group or name is empty or null");
        }
        throw new IllegalPropertyException("group or name is empty or null");
    }

    public static void decryptUsernamePassword(Properties driverProperties, @Nullable DriverPropertiesCipherer cipherer) {
        if (cipherer == null) {
            return;
        }
        String username = driverProperties.getProperty("username");
        if (Strings.isNotBlank(username)) {
            username = DataSources.decrypt(cipherer, username);
            driverProperties.setProperty("username", username);
        }
        String password = driverProperties.getProperty("password");
        if (Strings.isNotBlank(password)) {
            password = DataSources.decrypt(cipherer, password);
            driverProperties.setProperty("password", password);
        }
    }

    public static String decrypt(@Nullable DriverPropertiesCipherer cipherer, @NotEmpty String encryptedBase64Text) {
        return decrypt(cipherer, encryptedBase64Text, false);
    }

    /**
     * 用于对 username, password 解密
     *
     * @param cipherer
     * @param encryptedBase64Text
     * @return
     * @since 3.4.5
     */
    public static String decrypt(@Nullable DriverPropertiesCipherer cipherer, @NotEmpty String encryptedBase64Text, boolean loggerError) {
        if (Strings.isBlank(encryptedBase64Text)) {
            return null;
        }
        if (cipherer == null) {
            return encryptedBase64Text;
        }
        try {
            return cipherer.decrypt(encryptedBase64Text);
        } catch (IllegalArgumentException ex) {
            if (loggerError) {
                logger.warn(ex.getMessage(), ex);
            }
            return encryptedBase64Text;
        } catch (SecurityException ex) {
            if (loggerError) {
                logger.warn(ex.getMessage(), ex);
            }
            return encryptedBase64Text;
        }
    }

    /**
     * 获取DataSource配置项
     *
     * @param driverProps
     * @return
     * @since 3.4.6
     */
    public static String getDriverPropertiesForLog(Properties driverProps) {
        final StringBuilder driverPropsString = new StringBuilder(256);

        Collects.forEach(Collects.propertiesToStringMap(driverProps), new Consumer2<String, String>() {
            @Override
            public void accept(String key, String value) {
                if ("password".equals(key)) {
                    driverPropsString.append(key + "='*******',");
                } else {
                    driverPropsString.append(key + "='" + value + "',");
                }
            }
        });

        return driverPropsString.toString();
    }
}
