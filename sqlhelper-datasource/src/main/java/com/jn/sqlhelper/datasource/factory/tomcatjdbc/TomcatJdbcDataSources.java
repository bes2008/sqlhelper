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

package com.jn.sqlhelper.datasource.factory.tomcatjdbc;

import com.jn.langx.util.Maths;
import com.jn.langx.util.Strings;
import com.jn.langx.util.Throwables;
import com.jn.sqlhelper.datasource.DataSources;
import com.jn.sqlhelper.datasource.definition.DataSourceProperties;
import org.apache.tomcat.jdbc.pool.DataSourceFactory;

import javax.sql.DataSource;
import java.util.Properties;

import static com.jn.sqlhelper.datasource.factory.tomcatjdbc.TomcatJdbcDataSourcePropertyNames.*;

/**
 * ref: http://tomcat.apache.org/tomcat-8.5-doc/jdbc-pool.html
 */
public class TomcatJdbcDataSources {
    private TomcatJdbcDataSources() {
    }

    public static DataSource createDataSource(final DataSourceProperties properties) {
        DataSourceFactory dsf = new DataSourceFactory();
        Properties props = properties.getDriverProps();
        if (props == null) {
            props = new Properties();
        }

        String username = properties.getUsername();
        if (username != null) {
            props.setProperty(PROP_USERNAME, username);
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
            props.setProperty(PROP_DRIVER_CLASSNAME, driverClassName);
        }

        props.setProperty(PROP_AUTO_COMMIT, "" + properties.isAutoCommit());
        props.setProperty(PROP_READONLY, "" + properties.isReadOnly());
        String transactionIsolation = DataSources.getTransactionIsolation(properties.getTransactionIsolationName());
        if (Strings.isNotBlank(transactionIsolation)) {
            props.setProperty(PROP_TRANSACTION_ISOLATION, transactionIsolation);
        }
        String catalog = properties.getCatalog();
        if (catalog != null) {
            props.setProperty(PROP_CATALOG, catalog);
        }

        props.setProperty(PROP_INITIAL_SIZE, "" + properties.getInitialSize());
        props.setProperty(PROP_MIN_IDLE, "" + properties.getMinIdle());
        props.setProperty(PROP_MAX_IDLE, "" + Maths.max(8, properties.getMinIdle()));
        props.setProperty(PROP_MAX_ACTIVE, "" + Maths.max(8, properties.getMaxPoolSize()));


        String validationQuery = properties.getValidationQuery();
        if (validationQuery != null) {
            props.setProperty(PROP_VALIDATION_QUERY, validationQuery);
        }
        try {
            return dsf.createDataSource(props);
        } catch (Exception ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        }
    }

    public static DataSource createDataSource(Properties properties) {
        try {
            DataSourceFactory dsf = new DataSourceFactory();
            return dsf.createDataSource(properties);
        } catch (Exception ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        }
    }


}
