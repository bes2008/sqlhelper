package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.ReturnResultsLimitHandler;

/**
 * http://openbase.wikidot.com/openbase-sql:select-statements
 */
public class OpenbaseDialect extends AbstractDialect {
    public OpenbaseDialect() {
        setLimitHandler(new ReturnResultsLimitHandler());
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
        return false;
    }

    @Override
    public boolean isUseMaxForLimit() {
        return true;
    }
}
