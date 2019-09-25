package com.jn.sqlhelper.dialect.internal;

import com.jn.langx.annotation.Name;
import com.jn.sqlhelper.dialect.internal.limit.NoopLimitHandler;

@Name("covenant")
public class CovenantSQLDialect extends AbstractDialect{
    public CovenantSQLDialect() {
        setLimitHandler(new NoopLimitHandler());
    }

    @Override
    public boolean isSupportsLimit() {
        return false;
    }

    @Override
    public boolean isBindLimitParametersInReverseOrder() {
        return false;
    }

}
