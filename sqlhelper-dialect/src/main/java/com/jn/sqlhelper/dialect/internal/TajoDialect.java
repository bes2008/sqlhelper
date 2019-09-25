package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.NoopLimitHandler;

public class TajoDialect extends AbstractDialect {
    public TajoDialect() {
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
