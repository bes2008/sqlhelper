package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.LimitOffsetLimitHandler;

/**
 * https://www.brytlyt.com/documentation/data-manipulation-dml/select/
 * supports 2 styles limit syntax:
 * 1) limit $limit offset $offset
 * 2) offset $offset ROWS fetch FIRST|NEXT $limit ROWS ONLY
 */
public class BrytlytDialect extends AbstractDialect {
    public BrytlytDialect() {
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
