package com.fjn.helper.sql.dialect;

import com.fjn.helper.sql.dialect.internal.urlparser.UrlParser;

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
