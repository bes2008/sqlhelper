package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.LimitOnlyLimitHandler;

/**
 * https://bitnine.net/documentations/manual/agens_graph_developer_manual_en.html#sql-language
 */
public class AgensGraphDialect extends AbstractDialect {
    public AgensGraphDialect() {
        super();
        setLimitHandler(new LimitOnlyLimitHandler());
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return false;
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
