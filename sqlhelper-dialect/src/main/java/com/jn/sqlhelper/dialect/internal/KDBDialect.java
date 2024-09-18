package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.OracleXLimitHandler;

/**
 * 浪潮 k-db
 */
public class KDBDialect extends AbstractDialect {
    public KDBDialect() {
        super();
        setLimitHandler(new OracleXLimitHandler());
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return true;
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isBindLimitParametersInReverseOrder() {
        return true;
    }

    @Override
    public boolean isUseMaxForLimit() {
        return true;
    }
}
