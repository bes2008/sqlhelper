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

package com.jn.sqlhelper.dialect.internal.limit;

import com.jn.sqlhelper.dialect.pagination.RowSelection;

import java.sql.PreparedStatement;
import java.sql.SQLException;


public class NoopLimitHandler extends AbstractLimitHandler {
    public static final NoopLimitHandler INSTANCE = new NoopLimitHandler();

    @Override
    public String processSql(String sql,boolean isSubquery, boolean useLimitVariable, RowSelection selection) {
        return sql;
    }

    @Override
    public int bindLimitParametersAtStartOfQuery(RowSelection selection, PreparedStatement statement, int index) {
        return 0;
    }

    @Override
    public int bindLimitParametersAtEndOfQuery(RowSelection selection, PreparedStatement statement, int index) {
        return 0;
    }

    @Override
    public void setMaxRows(RowSelection selection, PreparedStatement statement) throws SQLException {
        if (LimitHelper.hasMaxRows(selection)) {
            int maxRows = selection.getLimit() + (int)(convertToFirstRowValue(LimitHelper.getFirstRow(selection)));
            if (maxRows < 0) {
                statement.setMaxRows(Integer.MAX_VALUE);
            } else {
                statement.setMaxRows(maxRows);
            }
        }
    }
}
