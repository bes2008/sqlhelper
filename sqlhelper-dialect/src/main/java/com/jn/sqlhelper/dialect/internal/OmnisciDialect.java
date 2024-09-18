
package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.LimitOffsetLimitHandler;

/**
 * https://www.omnisci.com/docs/latest/5_dml.html#select
 */
public class OmnisciDialect extends AbstractDialect {
    public OmnisciDialect() {
        super();
        setLimitHandler(new LimitOffsetLimitHandler().setHasOffsetRowsSuffix(true));
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
}
