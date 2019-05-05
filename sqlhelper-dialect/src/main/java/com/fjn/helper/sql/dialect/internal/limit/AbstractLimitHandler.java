package com.fjn.helper.sql.dialect.internal.limit;

import com.fjn.helper.sql.dialect.Dialect;
import com.fjn.helper.sql.dialect.RowSelection;

import java.sql.PreparedStatement;
import java.sql.SQLException;


public abstract class AbstractLimitHandler extends LimitHandler {
    @Override
    public Dialect getDialect() {
        return this.dialect;
    }


    protected int convertToFirstRowValue(int zeroBasedFirstResult) {
        return zeroBasedFirstResult;
    }

    public int bindLimitParametersAtStartOfQuery(RowSelection selection, PreparedStatement statement, int index)
            throws SQLException {
        return this.dialect.isBindLimitParametersFirst() ? bindLimitParameters(selection, statement, index) : 0;
    }

    public int bindLimitParametersAtEndOfQuery(RowSelection selection, PreparedStatement statement, int index)
            throws SQLException {
        return !this.dialect.isBindLimitParametersFirst() ? bindLimitParameters(selection, statement, index) : 0;
    }


    public void setMaxRows(RowSelection selection, PreparedStatement statement)
            throws SQLException {
    }


    protected final int bindLimitParameters(RowSelection selection, PreparedStatement statement, int index)
            throws SQLException {
        if ((!this.dialect.isSupportsVariableLimit()) || (!LimitHelper.hasMaxRows(selection))) {
            return 0;
        }
        int firstRow = convertToFirstRowValue(LimitHelper.getFirstRow(selection));
        int lastRow = getMaxOrLimit(selection);
        boolean hasFirstRow = (this.dialect.isSupportsLimitOffset()) && ((firstRow > 0) || (this.dialect.isForceLimitUsage()));
        boolean reverse = this.dialect.isBindLimitParametersInReverseOrder();
        if (hasFirstRow) {
            statement.setInt(index + (reverse ? 1 : 0), firstRow);
        }
        statement.setInt(index + ((reverse) || (!hasFirstRow) ? 0 : 1), lastRow);
        return hasFirstRow ? 2 : 1;
    }


    protected final int getMaxOrLimit(RowSelection selection) {
        int firstRow = convertToFirstRowValue(LimitHelper.getFirstRow(selection));
        int lastRow = selection.getLimit().intValue();
        return this.dialect.isUseMaxForLimit() ? lastRow + firstRow : lastRow;
    }
}
