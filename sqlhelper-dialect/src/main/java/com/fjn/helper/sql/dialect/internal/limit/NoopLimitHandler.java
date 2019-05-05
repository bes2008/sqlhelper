package com.fjn.helper.sql.dialect.internal.limit;

import com.fjn.helper.sql.dialect.RowSelection;

import java.sql.PreparedStatement;
import java.sql.SQLException;


public class NoopLimitHandler
        extends AbstractLimitHandler {
    public static final NoopLimitHandler INSTANCE = new NoopLimitHandler();


    public String processSql(String sql, RowSelection selection) {
        return sql;
    }

    public int bindLimitParametersAtStartOfQuery(RowSelection selection, PreparedStatement statement, int index) {
        return 0;
    }

    public int bindLimitParametersAtEndOfQuery(RowSelection selection, PreparedStatement statement, int index) {
        return 0;
    }

    public void setMaxRows(RowSelection selection, PreparedStatement statement) throws SQLException {
        if (LimitHelper.hasMaxRows(selection)) {
            int maxRows = selection.getLimit().intValue() + convertToFirstRowValue(LimitHelper.getFirstRow(selection));
            if (maxRows < 0) {
                statement.setMaxRows(Integer.MAX_VALUE);
            } else {
                statement.setMaxRows(maxRows);
            }
        }
    }
}
