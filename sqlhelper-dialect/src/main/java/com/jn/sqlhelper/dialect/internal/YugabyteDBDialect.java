package com.jn.sqlhelper.dialect.internal;

import com.jn.langx.annotation.Name;
import com.jn.sqlhelper.dialect.internal.limit.LimitOffsetLimitHandler;

@Name("yugbyte")
public class YugabyteDBDialect extends AbstractDialect {
    public YugabyteDBDialect() {
        super();
        LimitOffsetLimitHandler limitHandler = new LimitOffsetLimitHandler();
        limitHandler.setHasOffsetRowsSuffix(true);
        setLimitHandler(limitHandler);
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
