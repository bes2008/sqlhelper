package com.fjn.helper.sql.dialect.internal.limit;

import com.fjn.helper.sql.dialect.RowSelection;

import java.util.Locale;


public class TopLimitHandler
        extends AbstractLimitHandler {
    public String processSql(String sql, RowSelection selection) {
        if (LimitHelper.hasFirstRow(selection)) {
            throw new UnsupportedOperationException("query result offset is not supported");
        }

        int selectIndex = sql.toLowerCase(Locale.ROOT).indexOf("select");
        int selectDistinctIndex = sql.toLowerCase(Locale.ROOT).indexOf("select distinct");
        int insertionPoint = selectIndex + (selectDistinctIndex == selectIndex ? 15 : 6);


        StringBuilder sb = new StringBuilder(sql.length() + 8).append(sql);

        if (this.dialect.isSupportsVariableLimit()) {
            sb.insert(insertionPoint, " TOP ? ");
        } else {
            sb.insert(insertionPoint, " TOP " + getMaxOrLimit(selection) + " ");
        }

        return sb.toString();
    }
}
