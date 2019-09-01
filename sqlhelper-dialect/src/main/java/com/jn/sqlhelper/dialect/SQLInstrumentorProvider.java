package com.jn.sqlhelper.dialect;

import com.jn.langx.factory.Provider;
import com.jn.sqlhelper.dialect.conf.SQLInstrumentConfig;

public class SQLInstrumentorProvider implements Provider<SQLInstrumentConfig, SQLStatementInstrumentor> {
    private SQLStatementInstrumentor instrumentor;
    private static SQLInstrumentorProvider instance = new SQLInstrumentorProvider();

    private SQLInstrumentorProvider() {

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
                instrumentor = new SQLStatementInstrumentor();
                ret = instrumentor;
                ret.setConfig(config);
                ret.init();
            }
        }
        return ret;
    }

    public SQLStatementInstrumentor get() {
        return instrumentor;
    }
}
