

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

package com.github.fangjinuo.sqlhelper.dialect;

import com.github.fangjinuo.sqlhelper.dialect.internal.urlparser.UrlParser;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface Dialect extends UrlParser {
    /**
     * Does this dialect support some form of limiting query results
     * via a soI clause?
     *
     * @return True if this dialect supports some form of LIMIT.
     */
    boolean isSupportsLimit();

    /**
     * Generally if there is no, limit applied to a query we do not apply any limits
     * . to the soI query. This option forces that the limit be written to the sol query.
     * s ereturn True to force limit into soL query even if none specified in query, false otherwise
     */
    boolean isForceLimitUsage();

    /**
     * Does this dialect ' s LIMIT support (if any) additionally
     * support specifying an offset?
     *
     * @return True if the dialect supports an offset within the limit support
     */
    boolean isSupportsLimitOffset();

    /**
     * Does this dialect support bind variables (i.e., prepared statement
     * parameters) for its limit/offset?
     *
     * @return True if bind variables can be used; false otherwise.
     */
    boolean isSupportsVariableLimit();


    /**
     * Given a limit and an offset, apply the limit clause to the query.
     *
     * @param query The query to which to apply the limit.
     * @return The modified query statement with the limit applied.
     */
    String getLimitSql(String query, RowSelection rowSelection);

    /**
     * Whether bind parameter in reverse or not.
     *
     * Here assume the normal order is: $offset, $limit
     * so the reverse order is: $limit, $offset
     *
     * Based on the assume,
     * case 1:
     *  limit $offset, $limit  ==> reverse = false
     * case 2:
     *  limit $limit offset $offset ==> reverse = true
     * @return
     */
    boolean isBindLimitParametersInReverseOrder();

    boolean isBindLimitParametersFirst();

    int bindLimitParametersAtStartOfQuery(RowSelection paramRowSelection, PreparedStatement paramPreparedStatement, int paramInt)
            throws SQLException;

    int bindLimitParametersAtEndOfQuery(RowSelection paramRowSelection, PreparedStatement paramPreparedStatement, int paramInt)
            throws SQLException;

    boolean isUseMaxForLimit();

    int registerResultSetOutParameter(CallableStatement paramCallableStatement, int paramInt)
            throws SQLException;

    void setMaxRows(RowSelection paramRowSelection, PreparedStatement paramPreparedStatement)
            throws SQLException;

}
