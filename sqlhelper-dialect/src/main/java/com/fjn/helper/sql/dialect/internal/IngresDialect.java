package com.fjn.helper.sql.dialect.internal;

import com.fjn.helper.sql.dialect.internal.limit.LimitHelper;
import com.fjn.helper.sql.dialect.RowSelection;
import com.fjn.helper.sql.dialect.internal.limit.AbstractLimitHandler;
import com.fjn.helper.sql.dialect.internal.limit.LimitHandler;


public class IngresDialect extends AbstractDialect {
    private static final LimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
        @Override
        public String processSql(String sql, RowSelection selection) {
            String soff = " offset " + selection.getOffset();
            String slim = " fetch first " + getMaxOrLimit(selection) + " rows only";

            StringBuilder sb = new StringBuilder(sql.length() + soff.length() + slim.length()).append(sql);
            if (LimitHelper.hasFirstRow(selection)) {
                sb.append(soff);
            }
            if (LimitHelper.hasMaxRows(selection)) {
                sb.append(slim);
            }
            return sb.toString();
        }

        @Override
        public String getLimitString(String querySelect, int offset, int limit) {
            StringBuilder soff = new StringBuilder(" offset " + offset);
            StringBuilder slim = new StringBuilder(" fetch first " + limit + " rows only");

            StringBuilder sb = new StringBuilder(querySelect.length() + soff.length() + slim.length()).append(querySelect);
            if (offset > 0) {
                sb.append(soff);
            }
            if (limit > 0) {
                sb.append(slim);
            }
            return sb.toString();
        }
    };


    public IngresDialect() {
        super();
        setLimitHandler(LIMIT_HANDLER);
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return true;
    }
}
