package com.jn.sqlhelper.dialect.internal.limit;

import com.jn.sqlhelper.dialect.pagination.RowSelection;

import java.sql.PreparedStatement;
import java.sql.SQLException;


public abstract class AbstractLimitHandler extends LimitHandler {

    protected int offsetBased = 0;

    public void setOffsetBased(int offsetBased) {
        this.offsetBased = offsetBased;
    }

    protected long convertToFirstRowValue(long zeroBasedFirstResult) {
        return zeroBasedFirstResult + offsetBased;
    }

    @Override
    public int bindLimitParametersAtStartOfQuery(RowSelection selection, PreparedStatement statement, int index)
            throws SQLException {
        return getDialect().isBindLimitParametersFirst() ? bindLimitParameters(selection, statement, index) : 0;
    }

    @Override
    public int bindLimitParametersAtEndOfQuery(RowSelection selection, PreparedStatement statement, int index)
            throws SQLException {
        return !getDialect().isBindLimitParametersFirst() ? bindLimitParameters(selection, statement, index) : 0;
    }

    @Override
    public void setMaxRows(RowSelection selection, PreparedStatement statement)
            throws SQLException {
        if (selection.getMaxRows() >= 0) {
            statement.setMaxRows(selection.getMaxRows());
        }
    }


    private int bindLimitParameters(RowSelection selection, PreparedStatement statement, int index)
            throws SQLException {
        if ((!getDialect().isUseLimitInVariableMode()) || (!LimitHelper.hasMaxRows(selection))) {
            return 0;
        }
        long firstRow = convertToFirstRowValue(LimitHelper.getFirstRow(selection));
        int lastRow = getMaxOrLimit(selection);
        boolean hasFirstRow = getDialect().isSupportsLimitOffset() && ((firstRow > 0) || (getDialect().isForceLimitUsage()));
        boolean reverse = getDialect().isBindLimitParametersInReverseOrder();
        if (hasFirstRow) {
            statement.setInt(index + (reverse ? 1 : 0), (int)firstRow);
        }
        statement.setInt(index + ((reverse) || (!hasFirstRow) ? 0 : 1), lastRow);
        return hasFirstRow ? 2 : 1;
    }


    protected final int getMaxOrLimit(RowSelection selection) {
        long firstRow = convertToFirstRowValue(LimitHelper.getFirstRow(selection));
        int limit = selection.getLimit();
        return getDialect().isUseMaxForLimit() ? (limit + (int)firstRow) : limit;
    }
}
