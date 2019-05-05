package com.fjn.helper.sql.dialect.internal;

import com.fjn.helper.sql.dialect.internal.limit.LimitHelper;
import com.fjn.helper.sql.dialect.RowSelection;
import com.fjn.helper.sql.dialect.internal.limit.AbstractLimitHandler;


public class InterbaseDialect extends AbstractDialect {
    private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
        @Override
        public String processSql(String sql, RowSelection selection) {
            boolean hasOffset = LimitHelper.hasFirstRow(selection);
            return getLimitString(sql, hasOffset);
        }

        @Override
        public String getLimitString(String sql, boolean hasOffset) {
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
