package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.LimitOffsetLimitHandler;

/**
 * https://docs.citusdata.com/en/v9.1/develop/reference_sql.html
 */
public class CitusDialect extends AbstractDialect {

    public CitusDialect() {
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
