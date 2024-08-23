
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

import com.jn.sqlhelper.dialect.Dialect;
import com.jn.sqlhelper.dialect.pagination.RowSelection;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;


public abstract class LimitHandler {
    private Dialect dialect;

    public Dialect getDialect() {
        return this.dialect;
    }

    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }


    public abstract String processSql(String sql, RowSelection rowSelection);


    protected String getLimitString(String sql, long offset, int limit) {
        return getLimitString(sql, (offset > 0) || (getDialect().isForceLimitUsage()));
    }


    protected String getLimitString(String sql, boolean hasOffset) {
        throw new UnsupportedOperationException("Paged queries not supported by " + getClass().getName());
    }

    public abstract int bindLimitParametersAtStartOfQuery(RowSelection rowSelection, PreparedStatement preparedStatement, int index)
            throws SQLException;

    public abstract int bindLimitParametersAtEndOfQuery(RowSelection rowSelection, PreparedStatement preparedStatement, int index)
            throws SQLException;

    public abstract int rebuildLimitParametersAtStartOfQuery(RowSelection rowSelection, List queryParams, int index);

    public abstract int rebuildLimitParametersAtEndOfQuery(RowSelection rowSelection, List queryParams, int index);


    public abstract void setMaxRows(RowSelection rowSelection, PreparedStatement preparedStatement)
            throws SQLException;
}
