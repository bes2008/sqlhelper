package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.NoopLimitHandler;

/**
 * https://www.sadasengine.com/SQL_Reference/index.html
 */
public class SadasDialect extends AbstractDialect {
    public SadasDialect() {
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
