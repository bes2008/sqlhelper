package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.NoopLimitHandler;

/**
 * http://www.smallsql.de/doc/sqlsyntax.html#SELECT
 */
public class SmallDialect extends AbstractDialect {
    public SmallDialect() {
        setLimitHandler(new NoopLimitHandler());
    }

    @Override
    public boolean isSupportsLimit() {
        return false;
    }

    @Override
    public boolean isBindLimitParametersInReverseOrder() {
        return false;
    }
}
