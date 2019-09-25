package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.NoopLimitHandler;

public class RaimaDialect extends AbstractDialect {
    public RaimaDialect() {
        super();
        setLimitHandler(new NoopLimitHandler());
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return false;
    }

    @Override
    public boolean isSupportsLimit() {
        return false;
    }
}
