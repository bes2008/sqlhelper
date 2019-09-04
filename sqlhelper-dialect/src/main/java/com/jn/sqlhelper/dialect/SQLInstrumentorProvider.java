package com.jn.sqlhelper.dialect;

import com.jn.langx.factory.Provider;
import com.jn.sqlhelper.dialect.conf.SQLInstrumentConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.IdentityHashMap;

public class SQLInstrumentorProvider implements Provider<SQLInstrumentConfig, SQLStatementInstrumentor> {
    private static final Logger logger = LoggerFactory.getLogger(SQLInstrumentorProvider.class);
    private IdentityHashMap<SQLInstrumentConfig, SQLStatementInstrumentor> instrumentCache = new IdentityHashMap<SQLInstrumentConfig, SQLStatementInstrumentor>();
    private static SQLInstrumentorProvider instance = new SQLInstrumentorProvider();

    private SQLInstrumentorProvider() {
        logger.info("Initial the singleton SQL instrumentor provider");
    }

    public static SQLInstrumentorProvider getInstance() {
        return instance;
    }

    @Override
    public SQLStatementInstrumentor get(SQLInstrumentConfig config) {
        if (config == null) {
            config = SQLInstrumentConfig.DEFAULT;
        }
        SQLStatementInstrumentor instrumentor = instrumentCache.get(config);
        if (instrumentor != null) {
            return instrumentor;
        } else {
            synchronized (this) {
                instrumentor = instrumentCache.get(config);
                if (instrumentor == null) {
                    instrumentor = new SQLStatementInstrumentor();
                    if (config == SQLInstrumentConfig.DEFAULT) {
                        logger.warn("Initial the SQL instrument config use default value");
                    }
                    instrumentor.setConfig(config);
                    instrumentor.init();
                    instrumentCache.put(config, instrumentor);
                }
            }
        }
        return instrumentor;
    }

    public SQLStatementInstrumentor get() {
        return get(null);
    }
}
