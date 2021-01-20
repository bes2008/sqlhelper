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

package com.jn.sqlhelper.datasource.factory.druid;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.jn.langx.util.Maths;
import com.jn.langx.util.Strings;
import com.jn.langx.util.Throwables;
import com.jn.sqlhelper.common.transaction.utils.Isolation;
import com.jn.sqlhelper.common.transaction.utils.Transactions;
import com.jn.sqlhelper.datasource.DataSources;
import com.jn.sqlhelper.datasource.config.DataSourceProperties;
import com.jn.sqlhelper.common.security.DataSourcePropertiesCipherer;

import javax.sql.DataSource;
import java.util.Properties;

import static com.alibaba.druid.pool.DruidDataSourceFactory.*;

/**
 * 提供基于 hikaricp 的DataSource 构建工具
 * https://github.com/alibaba/druid/wiki/DruidDataSource%E9%85%8D%E7%BD%AE%E5%B1%9E%E6%80%A7%E5%88%97%E8%A1%A8
 *
 * @since 3.4.0
 */
public class AlibabaDruidDataSources {
    private AlibabaDruidDataSources() {
    }

    public static DataSource createDataSource(DataSourceProperties properties) {
        return createDataSource(properties, null);
    }

    /**
     * @since 3.4.5
     */
    public static DataSource createDataSource(DataSourceProperties properties, DataSourcePropertiesCipherer cipherer) {
        Properties props = properties.getDriverProps();
        if (props == null) {
            props = new Properties();
        }

        String username = properties.getUsername();
        if (Strings.isNotBlank(username)) {
            username = DataSources.decrypt(cipherer, username);
            props.setProperty(PROP_USERNAME, username);
        }

        String password = properties.getPassword();
        if (Strings.isNotBlank(password)) {
            password = DataSources.decrypt(cipherer, password);
            props.setProperty(PROP_PASSWORD, password);
        }

        String url = properties.getUrl();
        if (url != null) {
            props.setProperty(PROP_URL, url);
        }

        String driverClassName = properties.getDriverClassName();
        if (driverClassName != null) {
            props.setProperty(PROP_DRIVERCLASSNAME, driverClassName);
        }

        props.setProperty(PROP_DEFAULTAUTOCOMMIT, "" + properties.isAutoCommit());
        props.setProperty(PROP_DEFAULTREADONLY, "" + properties.isReadonly());
        Isolation isolation = Transactions.getTransactionIsolation(properties.getTransactionIsolation());
        if (Transactions.isValidIsolation(isolation)) {
            props.setProperty(PROP_DEFAULTTRANSACTIONISOLATION, isolation.getName());
        }

        String catalog = properties.getCatalog();
        if (catalog != null) {
            props.setProperty(PROP_DEFAULTCATALOG, catalog);
        }

        props.setProperty(PROP_INITIALSIZE, "" + properties.getInitialSize());
        props.setProperty(PROP_MINIDLE, "" + properties.getMinIdle());
        props.setProperty(PROP_MAXIDLE, "" + Maths.max(8, properties.getMinIdle()));
        props.setProperty(PROP_MAXACTIVE, "" + Maths.max(8, properties.getMaxPoolSize()));


        String validationQuery = properties.getValidationQuery();
        if (validationQuery != null) {
            props.setProperty(PROP_VALIDATIONQUERY, validationQuery);
        }

        try {
            return createDataSource(props);
        } catch (Exception ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        }
    }

    public static DataSource createDataSource(Properties properties) {
        return createDataSource(properties, null);
    }


    /**
     * @since 3.4.5
     */
    public static DataSource createDataSource(Properties properties, DataSourcePropertiesCipherer cipherer) {
        try {
            DataSources.decryptUsernamePassword(properties, cipherer);
            return DruidDataSourceFactory.createDataSource(properties);
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
