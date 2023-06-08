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

package com.jn.sqlhelper.datasource.factory.c3p0;

import com.jn.langx.util.Maths;
import com.jn.langx.util.Strings;
import com.jn.langx.util.Throwables;
import com.jn.sqlhelper.common.security.JdbcSecuritys;
import com.jn.sqlhelper.common.transaction.utils.Isolation;
import com.jn.sqlhelper.common.transaction.utils.Transactions;
import com.jn.sqlhelper.datasource.config.DataSourceProperties;
import com.jn.sqlhelper.common.security.DriverPropertiesCipher;
import com.mchange.v2.c3p0.DataSources;

import javax.sql.DataSource;
import java.util.Properties;

import static com.jn.sqlhelper.datasource.factory.c3p0.C3p0PropertyNames.*;


/**
 * 提供基于 hikaricp 的DataSource 构建工具
 * https://www.mchange.com/projects/c3p0/#using_datasources_factory
 */
public class C3p0DataSources {
    private C3p0DataSources() {
    }

    public static DataSource createDataSource(final DataSourceProperties properties) {
        return createDataSource(properties, JdbcSecuritys.getDefaultDriverPropertiesCipher());
    }

    /**
     * @since 3.4.5
     */
    public static DataSource createDataSource(final DataSourceProperties properties, DriverPropertiesCipher cipherer) {
        try {
            DataSource ds_unpooled = DataSources.unpooledDataSource(properties.getUrl(), properties.getUsername(), properties.getPassword());
            Properties props = properties.getDriverProps();

            if (props == null) {
                props = new Properties();
            }


            String username = properties.getUsername();
            if (Strings.isNotBlank(username)) {
                username = com.jn.sqlhelper.datasource.DataSources.decrypt(cipherer, username);
                props.setProperty(PROP_USER_NAME, username);
            }

            String password = properties.getPassword();
            if (Strings.isNotBlank(password)) {
                password = com.jn.sqlhelper.datasource.DataSources.decrypt(cipherer, password);
                props.setProperty(PROP_PASSWORD, password);
            }

            String url = properties.getUrl();
            if (url != null) {
                props.setProperty(PROP_URL, url);
            }

            String driverClassName = properties.getDriverClassName();
            if (driverClassName != null) {
                props.setProperty(PROP_DRIVER_CLASS_NAME, driverClassName);
            }

            props.setProperty(PROP_DEFAULT_AUTO_COMMIT, "" + properties.isAutoCommit());
            props.setProperty(PROP_DEFAULT_READ_ONLY, "" + properties.isReadonly());
            Isolation isolation = Transactions.getTransactionIsolation(properties.getTransactionIsolation());
            if (Transactions.isValidIsolation(isolation)) {
                props.setProperty(PROP_DEFAULT_TRANSACTION_ISOLATION, properties.getTransactionIsolation());
            }

            String catalog = properties.getCatalog();
            if (catalog != null) {
                props.setProperty(PROP_DEFAULT_CATALOG, catalog);
            }

            String schema = properties.getSchema();
            if (schema != null) {
                props.setProperty(PROP_DEFAULT_SCHEMA, schema);
            }

            props.setProperty(PROP_INITIAL_SIZE, "" + properties.getInitialSize());
            props.setProperty(PROP_MAX_POOL_SIZE, "" + Maths.max(8, properties.getMaxPoolSize()));

            return DataSources.pooledDataSource(ds_unpooled, properties.getName(), props);
        } catch (Exception ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        }
    }

    public static DataSource createDataSource(Properties properties) {
        return createDataSource(properties, JdbcSecuritys.getDefaultDriverPropertiesCipher());
    }

    /**
     * @since 3.4.5
     */
    public static DataSource createDataSource(Properties properties, DriverPropertiesCipher cipherer) {
        try {
            DataSource ds_unpooled = DataSources.unpooledDataSource();
            com.jn.sqlhelper.datasource.DataSources.decryptUsernamePassword(properties, cipherer);
            return DataSources.pooledDataSource(ds_unpooled, properties);
        } catch (Exception ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        }
    }

    public static DataSourceProperties toDataSourceProperties(Properties properties) {
        DataSourceProperties dataSourceProperties = new DataSourceProperties();
        dataSourceProperties.setDriverProps(properties);
        return dataSourceProperties;
    }
}
