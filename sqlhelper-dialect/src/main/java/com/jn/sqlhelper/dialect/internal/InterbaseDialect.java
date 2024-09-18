package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.pagination.RowSelection;
import com.jn.sqlhelper.dialect.internal.limit.AbstractLimitHandler;
import com.jn.sqlhelper.dialect.internal.limit.LimitHelper;


public class InterbaseDialect extends AbstractDialect {
    private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
        @Override
        public String processSql(String sql, boolean isSubquery, boolean useLimitVariable, RowSelection selection) {
            boolean hasOffset = LimitHelper.hasFirstRow(selection);
            return getLimitString(sql, isSubquery, useLimitVariable, hasOffset);
        }

        @Override
        public String getLimitString(String sql, boolean isSubquery, boolean useLimitVariable, boolean hasOffset) {
            return sql + " rows ?";
        }
    };


    public InterbaseDialect() {
        super();
        setLimitHandler(LIMIT_HANDLER);
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isBindLimitParametersFirst() {
        return false;
    }

    @Override
    public boolean isBindLimitParametersInReverseOrder() {
        return false;
    }
}
