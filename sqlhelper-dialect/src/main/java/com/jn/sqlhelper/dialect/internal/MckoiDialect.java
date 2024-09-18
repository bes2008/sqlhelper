package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.LimitOnlyLimitHandler;

public class MckoiDialect extends AbstractDialect {
    public MckoiDialect() {
        super();
        setLimitHandler(new LimitOnlyLimitHandler());
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return false;
    }

}
