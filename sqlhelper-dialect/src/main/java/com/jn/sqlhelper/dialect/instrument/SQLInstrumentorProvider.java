package com.jn.sqlhelper.dialect.instrument;

import com.jn.langx.factory.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.IdentityHashMap;

public class SQLInstrumentorProvider implements Provider<SQLInstrumentorConfig, SQLStatementInstrumentor> {
    private static final Logger logger = LoggerFactory.getLogger(SQLInstrumentorProvider.class);
    private IdentityHashMap<SQLInstrumentorConfig, SQLStatementInstrumentor> instrumentCache = new IdentityHashMap<SQLInstrumentorConfig, SQLStatementInstrumentor>();
    private static SQLInstrumentorProvider instance = new SQLInstrumentorProvider();

    private SQLInstrumentorProvider() {
        logger.info("Initial the singleton SQL instrumentor provider");
    }

    public static SQLInstrumentorProvider getInstance() {
        return instance;
    }

    @Override
    public SQLStatementInstrumentor get(SQLInstrumentorConfig config) {
        if (config == null) {
            config = SQLInstrumentorConfig.DEFAULT;
        }
        SQLStatementInstrumentor instrumentor = instrumentCache.get(config);
        if (instrumentor != null) {
            return instrumentor;
        } else {
            synchronized (this) {
                instrumentor = instrumentCache.get(config);
                if (instrumentor == null) {
                    instrumentor = new SQLStatementInstrumentor();
                    if (config == SQLInstrumentorConfig.DEFAULT) {
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
