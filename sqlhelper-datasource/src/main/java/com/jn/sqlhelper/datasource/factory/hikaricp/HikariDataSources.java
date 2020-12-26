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

package com.jn.sqlhelper.datasource.factory.hikaricp;

import com.jn.langx.util.Emptys;
import com.jn.langx.util.Strings;
import com.jn.langx.util.reflect.Reflects;
import com.jn.sqlhelper.datasource.DataSources;
import com.jn.sqlhelper.datasource.definition.DataSourceProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Properties;

public class HikariDataSources {
    private static final Logger logger = LoggerFactory.getLogger(HikariDataSources.class);

    private HikariDataSources() {
    }

    public static DataSource createDataSource(final DataSourceProperties props) {
        Properties driverProps = props.getDriverProps();
        HikariConfig config = null;
        if (Emptys.isNotEmpty(driverProps)) {
            config = new HikariConfig(driverProps);
        } else {
            config = new HikariConfig();
        }
        config.setDriverClassName(props.getDriverClassName());
        config.setJdbcUrl(props.getUrl());
        config.setUsername(props.getUsername());
        config.setPassword(props.getPassword());
        config.setPoolName(props.getName());
        config.setCatalog(props.getCatalog());
        Reflects.invokePublicMethod(config, "setSchema", new Class[]{String.class}, new Object[]{props.getSchema()}, true, false);
        config.setLeakDetectionThreshold(props.getLeakDetectionThresholdInMills());
        config.setConnectionTimeout(props.getConnectionTimeoutInMills());
        config.setValidationTimeout(props.getValidationTimeoutInMills());
        config.setConnectionInitSql(props.getValidationQuery());
        config.setIdleTimeout(props.getIdleTimeoutInMills());
        config.setMaxLifetime(props.getMaxLifetimeInMills());
        config.setMaximumPoolSize(props.getMaxPoolSize());
        config.setMinimumIdle(props.getMinIdle());
        config.setAutoCommit(props.isAutoCommit());
        String transactionIsolation = DataSources.getTransactionIsolation(props.getTransactionIsolation());
        if (Strings.isNotBlank(transactionIsolation)) {
            config.setTransactionIsolation("TRANSACTION_" + transactionIsolation);
        }

        config.setReadOnly(props.isReadOnly());
        return new HikariDataSource(config);
    }

    public static DataSource createDataSource(final Properties props) {
        return new HikariDataSource(new HikariConfig(props));
    }

}
