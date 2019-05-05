package com.fjn.helper.sql.dialect.internal;

import com.fjn.helper.sql.dialect.internal.limit.LimitHelper;
import com.fjn.helper.sql.dialect.RowSelection;
import com.fjn.helper.sql.dialect.internal.limit.AbstractLimitHandler;


public class RDMSOS2200Dialect extends AbstractDialect {

    public RDMSOS2200Dialect() {
        super();
        setLimitHandler(new AbstractLimitHandler() {
            @Override
            public String processSql(String sql, RowSelection selection) {
                boolean hasOffset = LimitHelper.hasFirstRow(selection);
                if (hasOffset) {
                    throw new UnsupportedOperationException("query result offset is not supported");
                }
                return sql + " fetch first " + getMaxOrLimit(selection) + " rows only ";
            }
        });
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return false;
    }

    @Override
    public boolean isSupportsVariableLimit() {
        return false;
    }
}
