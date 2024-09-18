package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.LimitOffsetLimitHandler;

/**
 * https://crate.io/docs/sql-99/en/latest/chapters/29.html#retrieval-with-like
 */
public class CrateDialect extends AbstractDialect {
    public CrateDialect() {
        super();
        setLimitHandler(new LimitOffsetLimitHandler());
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return true;
    }

    @Override
    public boolean isBindLimitParametersInReverseOrder() {
        return true;
    }
}
