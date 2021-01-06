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

package com.jn.sqlhelper.datasource.factory.dbcp2;

import com.jn.langx.util.Maths;
import com.jn.langx.util.Strings;
import com.jn.langx.util.Throwables;
import com.jn.sqlhelper.common.transaction.Transactions;
import com.jn.sqlhelper.datasource.DataSources;
import com.jn.sqlhelper.datasource.config.DataSourceProperties;
import org.apache.commons.dbcp2.BasicDataSourceFactory;

import javax.sql.DataSource;
import java.util.Properties;

import static com.jn.sqlhelper.datasource.factory.dbcp2.Dbcp2PropertyNames.*;


/**
 * http://commons.apache.org/proper/commons-dbcp/configuration.html
 */
public class Dbcp2DataSources {
    private Dbcp2DataSources() {
    }

    public static DataSource createDataSource(DataSourceProperties properties) {
        Properties props = properties.getDriverProps();
        if (props == null) {
            props = new Properties();
        }


        String username = properties.getUsername();
        if (username != null) {
            props.setProperty(PROP_USER_NAME, username);
        }

        String password = properties.getPassword();
        if (password != null) {
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
        props.setProperty(PROP_DEFAULT_READ_ONLY, "" + properties.isReadOnly());
        String transactionIsolation = Transactions.getTransactionIsolation(properties.getTransactionIsolation()).getName();
        if (Strings.isNotBlank(transactionIsolation)) {
            props.setProperty(PROP_DEFAULT_TRANSACTION_ISOLATION, transactionIsolation);
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
        props.setProperty(PROP_MIN_IDLE, "" + properties.getMinIdle());
        props.setProperty(PROP_MAX_IDLE, "" + Maths.max(8, properties.getMinIdle()));
        props.setProperty(PROP_MAX_TOTAL, "" + Maths.max(8, properties.getMaxPoolSize()));


        String validationQuery = properties.getValidationQuery();
        if (validationQuery != null) {
            props.setProperty(PROP_VALIDATION_QUERY, validationQuery);
        }

        props.setProperty(PROP_MAX_CONN_LIFETIME_MILLIS, "" + properties.getMaxLifetimeInMills());


        try {
            return createDataSource(props);
        } catch (Exception ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        }
    }

    public static DataSource createDataSource(Properties props) {
        try {
            return BasicDataSourceFactory.createDataSource(props);
        } catch (Exception ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        }
    }

    public static DataSourceProperties toDataSourceProperties(Properties properties) {
        DataSourceProperties p = new DataSourceProperties();
        p.setDriverProps(properties);
        return p;
    }
}
