package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.TopLimitHandler;

/**
 * https://docs.faircom.com/doc/sqlref/select.htm
 */
public class CTreeDialect extends AbstractDialect {
    public CTreeDialect() {
        super();
        setLimitHandler(new TopLimitHandler().setUseSkipTop(true));
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
    public boolean isBindLimitParametersFirst() {
        return true;
    }
}
