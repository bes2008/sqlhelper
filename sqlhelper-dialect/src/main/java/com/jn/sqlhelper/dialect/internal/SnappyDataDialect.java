package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.LimitOnlyLimitHandler;

public class SnappyDataDialect extends AbstractDialect {
    public SnappyDataDialect() {
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
