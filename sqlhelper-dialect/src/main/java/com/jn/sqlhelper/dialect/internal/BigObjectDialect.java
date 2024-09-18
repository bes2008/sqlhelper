package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.LimitOffsetLimitHandler;

/**
 * http://docs.bigobject.io/Basic_Data_Management/SELECT/index.html
 * <p>
 * * supports 2 styles limit syntax:
 * 1) limit $limit offset $offset
 * 2) limit $offset, $limit
 * <p>
 * We use 1)
 */
public class BigObjectDialect extends AbstractDialect {
    public BigObjectDialect() {
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
