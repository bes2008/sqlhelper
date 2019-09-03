package com.jn.sqlhelper.dialect;

import com.jn.langx.factory.Provider;
import com.jn.sqlhelper.dialect.conf.SQLInstrumentConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLInstrumentorProvider implements Provider<SQLInstrumentConfig, SQLStatementInstrumentor> {
    private static final Logger logger = LoggerFactory.getLogger(SQLInstrumentorProvider.class);
    private SQLStatementInstrumentor instrumentor;
    private static SQLInstrumentorProvider instance = new SQLInstrumentorProvider();

    private SQLInstrumentorProvider() {
        logger.info("Initial the singleton SQL instrumentor provider");
    }

    public static SQLInstrumentorProvider getInstance() {
        return instance;
    }

    @Override
    public SQLStatementInstrumentor get(SQLInstrumentConfig config) {
        SQLStatementInstrumentor ret;
        if (instrumentor != null) {
            return instrumentor;
        } else {
            synchronized (this) {
                if (instrumentor == null) {
                    instrumentor = new SQLStatementInstrumentor();
                    if (config == null) {
                        logger.warn("Initial the SQL instrument config use default value");
                        config = new SQLInstrumentConfig();
                    }
                    instrumentor.setConfig(config);
                    instrumentor.init();
                }
            }
        }
        return instrumentor;
    }

    public SQLStatementInstrumentor get() {
        return get(null);
    }
}
