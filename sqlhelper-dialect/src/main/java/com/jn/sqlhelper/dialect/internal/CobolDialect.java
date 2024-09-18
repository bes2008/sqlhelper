package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.LimitCommaLimitHandler;

public class CobolDialect extends AbstractDialect {
    public CobolDialect() {
        super();
        setLimitHandler(new LimitCommaLimitHandler());
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return true;
    }
}