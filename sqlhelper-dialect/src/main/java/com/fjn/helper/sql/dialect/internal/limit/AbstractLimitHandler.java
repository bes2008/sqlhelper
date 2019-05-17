
/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the LGPL, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fjn.helper.sql.dialect.internal.limit;

import com.fjn.helper.sql.dialect.RowSelection;

import java.sql.PreparedStatement;
import java.sql.SQLException;


public abstract class AbstractLimitHandler extends LimitHandler {

    protected int convertToFirstRowValue(int zeroBasedFirstResult) {
        return zeroBasedFirstResult;
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
    }


    private int bindLimitParameters(RowSelection selection, PreparedStatement statement, int index)
            throws SQLException {
        if ((!getDialect().isSupportsVariableLimit()) || (!LimitHelper.hasMaxRows(selection))) {
            return 0;
        }
        int firstRow = convertToFirstRowValue(LimitHelper.getFirstRow(selection));
        int lastRow = getMaxOrLimit(selection);
        boolean hasFirstRow = (getDialect().isSupportsLimitOffset()) && ((firstRow > 0) || (getDialect().isForceLimitUsage()));
        boolean reverse = getDialect().isBindLimitParametersInReverseOrder();
        if (hasFirstRow) {
            statement.setInt(index + (reverse ? 1 : 0), firstRow);
        }
        statement.setInt(index + ((reverse) || (!hasFirstRow) ? 0 : 1), lastRow);
        return hasFirstRow ? 2 : 1;
    }


    protected final int getMaxOrLimit(RowSelection selection) {
        int firstRow = convertToFirstRowValue(LimitHelper.getFirstRow(selection));
        int lastRow = selection.getLimit();
        return getDialect().isUseMaxForLimit() ? lastRow + firstRow : lastRow;
    }
}
