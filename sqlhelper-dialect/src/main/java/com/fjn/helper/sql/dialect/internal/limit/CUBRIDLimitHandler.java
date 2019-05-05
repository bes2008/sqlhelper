package com.fjn.helper.sql.dialect.internal.limit;

import com.fjn.helper.sql.dialect.RowSelection;


public class CUBRIDLimitHandler
        extends AbstractLimitHandler {
    public String processSql(String sql, RowSelection selection) {
        if (LimitHelper.useLimit(getDialect(), selection)) {


            boolean useLimitOffset = LimitHelper.hasFirstRow(selection);
            return sql + (useLimitOffset ? " limit ?, ?" : " limit ?");
        }

        return sql;
    }
}
