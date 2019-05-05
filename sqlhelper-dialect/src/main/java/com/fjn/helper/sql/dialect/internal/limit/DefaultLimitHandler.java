package com.fjn.helper.sql.dialect.internal.limit;

import com.fjn.helper.sql.dialect.RowSelection;
import com.fjn.helper.sql.dialect.internal.AbstractDialect;


public class DefaultLimitHandler
        extends AbstractLimitHandler {
    public DefaultLimitHandler(AbstractDialect dialect) {
        setDialect(dialect);
    }


    public DefaultLimitHandler() {
    }


    public String processSql(String sql, RowSelection selection) {
        boolean useLimitOffset = (this.dialect.isSupportsLimit()) && (this.dialect.isSupportsLimitOffset()) && (LimitHelper.hasFirstRow(selection)) && (LimitHelper.hasMaxRows(selection));
        return getLimitString(sql, useLimitOffset ?

                        LimitHelper.getFirstRow(selection) : 0,
                getMaxOrLimit(selection));
    }
}
