package com.jn.sqlhelper.ebean;

import com.jn.sqlhelper.dialect.Dialect;
import com.jn.sqlhelper.dialect.DialectRegistry;
import com.jn.sqlhelper.dialect.instrument.SQLStatementInstrumentor;
import com.jn.sqlhelper.dialect.instrument.SQLInstrumentConfig;
import io.ebean.config.AutoConfigure;
import io.ebean.config.ServerConfig;
import io.ebean.config.dbplatform.DatabasePlatform;
import io.ebean.datasource.DataSourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class SqlLimiterAutoConfigure implements AutoConfigure {
    private static final Logger logger = LoggerFactory.getLogger(SqlLimiterAutoConfigure.class);

    @Override
    public void preConfigure(ServerConfig serverConfig) {

    }

    @Override
    public void postConfigure(ServerConfig serverConfig) {
        Field field = null;
        try {
            field = DatabasePlatform.class.getDeclaredField("sqlLimiter");
            field.setAccessible(true);
        } catch (Throwable ex) {
            logger.warn("Can't find field [{}] in class {}", "sqlLimiter", DatabasePlatform.class.getCanonicalName());
        }
        if (field == null) {
            return;
        }
        SQLStatementInstrumentor instrumentor = new SQLStatementInstrumentor();
        SQLInstrumentConfig instrumentConfig = null;

        String databaseId = null;
        // guess database id
        String databaseName = serverConfig.getDatabasePlatformName();
        if (databaseName != null) {
            databaseId = DialectRegistry.guessDatabaseId(databaseName);
        }
        if (databaseId == null) {
            DataSourceConfig dataSourceConfig = serverConfig.getDataSourceConfig();
            databaseId = DialectRegistry.guessDatabaseId(dataSourceConfig.getDriver());

            if (databaseId == null) {
                databaseId = DialectRegistry.guessDatabaseId(dataSourceConfig.getUrl());
            }
        }

        if (databaseId != null) {
            instrumentConfig = new SQLInstrumentConfig();
            instrumentConfig.setDialect(databaseId);
        }
        if (instrumentConfig != null) {
            instrumentor.setConfig(instrumentConfig);
        }
        EBeanCommonSqlLimiter commonSqlLimiter = new EBeanCommonSqlLimiter();
        commonSqlLimiter.setInstrumentor(instrumentor);
        DatabasePlatform databasePlatform = serverConfig.getDatabasePlatform();
        try {
            field.set(databasePlatform, commonSqlLimiter);
        } catch (Throwable ex) {
            logger.warn("Configure a common SqlLimit [{}] for EBean fail,", EBeanCommonSqlLimiter.class.getCanonicalName());
        }

        // change it's quote
        if (databaseId != null) {
            try {
                Dialect dialect = DialectRegistry.getInstance().getDialectByName(databaseId);
                Field openQuote = DatabasePlatform.class.getDeclaredField("openQuote");
                openQuote.setAccessible(true);
                Field closeQuote = DatabasePlatform.class.getDeclaredField("closeQuote");
                openQuote.setAccessible(true);
                openQuote.set(databasePlatform, "" + dialect.getBeforeQuote());
                closeQuote.set(databasePlatform, "" + dialect.getAfterQuote());
            } catch (Throwable ex) {
                logger.warn("Set openQuote, closeQuote fail for databaseId {}", databaseId);
            }
        }

    }
}
