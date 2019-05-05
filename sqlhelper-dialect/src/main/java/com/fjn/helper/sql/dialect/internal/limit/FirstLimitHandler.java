package com.fjn.helper.sql.dialect.internal.limit;

import com.fjn.helper.sql.dialect.RowSelection;


public class FirstLimitHandler
        extends LegacyFirstLimitHandler {
    public String processSql(String sql, RowSelection selection) {
        boolean hasOffset = LimitHelper.hasFirstRow(selection);
        if (hasOffset) {
            throw new UnsupportedOperationException("query result offset is not supported");
        }
        return super.processSql(sql, selection);
    }
}
