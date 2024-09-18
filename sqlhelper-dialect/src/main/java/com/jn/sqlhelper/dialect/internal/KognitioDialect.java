package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.LimitOnlyLimitHandler;

/**
 * https://kognitio.com/documentation/latest/sqlref/select.html
 */
public class KognitioDialect extends AbstractDialect {
    public KognitioDialect() {
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
