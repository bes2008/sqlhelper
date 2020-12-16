package com.jn.agileway.jdbc.datasource.factory.hikaricp;

import com.jn.agileway.jdbc.Jdbcs;
import com.jn.agileway.jdbc.datasource.factory.DataSourceProperties;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.reflect.Reflects;
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
        int txIsoLevel = -1;
        try {
            txIsoLevel = Jdbcs.getTransactionIsolation(props.getTransactionIsolationName());
        } catch (Throwable t) {
            logger.error("parse jdbc transaction isolation fail: {}", t.getMessage(), t);
        } finally {
            if (txIsoLevel == -1) {
                props.setTransactionIsolationName("TRANSACTION_READ_COMMITTED");
            }
        }
        config.setTransactionIsolation(props.getTransactionIsolationName());
        config.setReadOnly(props.isReadOnly());
        return new HikariDataSource(config);
    }

    public static DataSource createDataSource(final Properties props) {
        return new HikariDataSource(new HikariConfig(props));
    }

}
