package com.fjn.helper.sql.dialect.internal.limit;

import com.fjn.helper.sql.dialect.RowSelection;

public class SQL2008StandardLimitHandler
        extends AbstractLimitHandler {
    public static final SQL2008StandardLimitHandler INSTANCE = new SQL2008StandardLimitHandler();


    public String processSql(String sql, RowSelection selection) {
        if (LimitHelper.useLimit(getDialect(), selection)) {
            return sql + (LimitHelper.hasFirstRow(selection) ? " offset ? rows fetch next ? rows only" : " fetch first ? rows only");
        }


        return sql;
    }
}
