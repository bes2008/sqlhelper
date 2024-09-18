package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.OffsetFetchFirstOnlyLimitHandler;


public class IngresDialect extends AbstractDialect {
    public IngresDialect() {
        super();
        setLimitHandler(new OffsetFetchFirstOnlyLimitHandler());
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
