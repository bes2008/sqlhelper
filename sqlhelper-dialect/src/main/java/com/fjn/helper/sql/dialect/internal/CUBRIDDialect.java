package com.fjn.helper.sql.dialect.internal;

import com.fjn.helper.sql.dialect.internal.limit.CUBRIDLimitHandler;

public class CUBRIDDialect extends AbstractDialect {
    public CUBRIDDialect() {
        super();
        setLimitHandler(new CUBRIDLimitHandler());
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }
}
