package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.OffsetFetchFirstOnlyLimitHandler;

/**
 * https://www.cockroachlabs.com/docs/stable/limit-offset.html
 */
public class CockroachDialect extends AbstractDialect {
    public CockroachDialect() {
        super();
        setLimitHandler(new OffsetFetchFirstOnlyLimitHandler());
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return true;
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }
}
