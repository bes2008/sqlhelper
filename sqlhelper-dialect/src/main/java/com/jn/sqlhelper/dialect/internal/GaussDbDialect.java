package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.annotation.Name;
import com.jn.sqlhelper.dialect.internal.limit.LimitOffsetLimitHandler;

@Name("gaussdb")
public class GaussDbDialect extends AbstractDialect {
    public GaussDbDialect() {
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
