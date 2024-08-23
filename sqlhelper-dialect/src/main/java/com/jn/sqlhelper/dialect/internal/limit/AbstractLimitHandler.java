package com.jn.sqlhelper.dialect.internal.limit;

import com.jn.sqlhelper.dialect.pagination.RowSelection;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;


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
    public int rebuildLimitParametersAtStartOfQuery(RowSelection selection, List queryParams, int index) {
        return getDialect().isBindLimitParametersFirst() ? rebuildLimitParameters(selection, queryParams, index) : 0;
    }

    @Override
    public int rebuildLimitParametersAtEndOfQuery(RowSelection selection, List queryParams, int index) {
        return !getDialect().isBindLimitParametersFirst() ? rebuildLimitParameters(selection, queryParams, index) : 0;
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
        int firstRow = (int)convertToFirstRowValue(LimitHelper.getFirstRow(selection));
        int lastRow = getMaxOrLimit(selection);
        boolean hasFirstRow = getDialect().isSupportsLimitOffset() && ((firstRow > 0) || (getDialect().isForceLimitUsage()));
        boolean reverse = getDialect().isBindLimitParametersInReverseOrder();
        if (hasFirstRow) {
            statement.setInt(index + (reverse ? 1 : 0), firstRow);
        }
        statement.setInt(index + ((reverse) || (!hasFirstRow) ? 0 : 1), lastRow);
        return hasFirstRow ? 2 : 1;
    }

    private int rebuildLimitParameters(RowSelection selection, List queryParams, int index) {
        if ((!getDialect().isUseLimitInVariableMode()) || (!LimitHelper.hasMaxRows(selection))) {
            return 0;
        }
        int firstRow = (int)convertToFirstRowValue(LimitHelper.getFirstRow(selection));
        int lastRow = getMaxOrLimit(selection);
        boolean hasFirstRow = getDialect().isSupportsLimitOffset() && ((firstRow > 0) || (getDialect().isForceLimitUsage()));
        boolean reverse = getDialect().isBindLimitParametersInReverseOrder();

        int indexOfLastRow = index + ((reverse) || (!hasFirstRow) ? 0 : 1);
        if (hasFirstRow) {
            int indexOfFirstRow =index + (reverse ? 1 : 0);
            if(indexOfFirstRow < indexOfLastRow){
                queryParams.add(indexOfFirstRow, firstRow);
                queryParams.add(indexOfLastRow, lastRow);
            }else{
                queryParams.add(indexOfLastRow, lastRow);
                queryParams.add(indexOfFirstRow, firstRow);
            }
        }else {
            queryParams.add(indexOfLastRow, lastRow);
        }
        return hasFirstRow ? 2 : 1;
    }



    protected final int getMaxOrLimit(RowSelection selection) {
        long firstRow = convertToFirstRowValue(LimitHelper.getFirstRow(selection));
        int limit = selection.getLimit();
        return getDialect().isUseMaxForLimit() ? (limit + (int)firstRow) : limit;
    }
}
