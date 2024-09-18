package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.LimitCommaLimitHandler;

public class DbfDialect extends AbstractDialect {
    public DbfDialect() {
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
