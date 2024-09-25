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

package com.jn.sqlhelper.dialect;

import com.jn.sqlhelper.dialect.internal.limit.LimitHandler;
import com.jn.sqlhelper.dialect.likeescaper.LikeEscaper;
import com.jn.sqlhelper.dialect.pagination.RowSelection;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public interface Dialect extends LikeEscaper {
    String getDatabaseId();
    LimitHandler getLimitHandler();
    /**
     * Does this dialect support some form of limiting query results
     * via a sql clause?
     *
     * @return True if this dialect supports some form of LIMIT.
     */
    boolean isSupportsLimit();

    /**
     * Generally if there is no, limit applied to a query we do not apply any limits
     * to the sql query. This option forces that the limit be written to the sql query.
     *
     * @return True to force limit into soL query even if none specified in query, false otherwise
     */
    boolean isForceLimitUsage();

    /**
     * Does this dialect's LIMIT support (if any) additionally
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

    boolean isSupportsVariableLimitInSubquery();

    /**
     * Whether set limit or offset with placeholder '?'
     *
     * @return true if use '?' for limit or offset variable, else false
     */
    boolean isUseLimitInVariableMode();

    boolean isUseLimitInVariableMode(boolean isSubquery);

    /**
     * Whether set limit or offset with placeholder '?'
     */
    void setUseLimitInVariableMode(boolean variableMode);

    /**
     * Given a limit and an offset, apply the limit clause to the query.
     *
     * @param query The query to which to apply the limit.
     * @return The modified query statement with the limit applied.
     */
    String getLimitSql(String query, RowSelection rowSelection);
    String getLimitSql(String query, boolean isSubQuery, RowSelection rowSelection);

    /**
     * 创建分页SQL
     * @param query 要处理的query sql
     * @param isSubQuery 该sql 是否位于子查询的位置
     * @param useLimitVariable 创建分页sql时，是否让 offset, limit使用 `?`
     * @param rowSelection
     * @return 生成的分页SQL
     */
    String getLimitSql(String query, boolean isSubQuery, boolean useLimitVariable, RowSelection rowSelection);

    /**
     * Whether bind parameter in reverse or not.
     * <p>
     * Here assume the normal order is: $offset, $limit
     * so the reverse order is: $limit, $offset
     * <p>
     * Based on the assume,
     * case 1:
     * limit $offset, $limit  ==> reverse = false
     * case 2:
     * limit $limit offset $offset ==> reverse = true
     */
    boolean isBindLimitParametersInReverseOrder();

    /**
     * limit parameter will be bind in the first or not,
     * if first ,the {@link #bindLimitParametersAtStartOfQuery(RowSelection, PreparedStatement, int)} will be execute
     * else the {@link #bindLimitParametersAtEndOfQuery(RowSelection, PreparedStatement, int)} will be execute
     */
    boolean isBindLimitParametersFirst();

    int bindLimitParametersAtStartOfQuery(RowSelection paramRowSelection, PreparedStatement paramPreparedStatement, int index)
            throws SQLException;

    int bindLimitParametersAtEndOfQuery(RowSelection paramRowSelection, PreparedStatement paramPreparedStatement, int index)
            throws SQLException;

    List rebuildParameters(RowSelection paramRowSelection, List queryParams);
    List rebuildParameters(boolean isSubquery, RowSelection paramRowSelection, List queryParams);

    /**
     * 对分页SQL语句重新设置参数列表
     * @param isSubquery 当前分页SQL是否位于子查询位置
     * @param useLimitVaribale 当前分页SQL的offset, limit参数是否使用 `?`
     * @param paramRowSelection
     * @param queryParams 原始参数
     * @return 重建后的参数
     */
    List rebuildParameters(boolean isSubquery, boolean useLimitVaribale, RowSelection paramRowSelection, List queryParams);
    boolean isUseMaxForLimit();

    int registerResultSetOutParameter(CallableStatement paramCallableStatement, int paramInt)
            throws SQLException;

    void setMaxRows(RowSelection paramRowSelection, PreparedStatement paramPreparedStatement)
            throws SQLException;

    public enum IdentifierCase {
        // identifier 在 数据库中，是否会被自动转换为 大写形式
        UPPER_CASE,
        // identifier 在 数据库中，是否会被自动转换为 小写形式
        LOWER_CASE,
        // 忽略大小写，无所谓，使用时加不加引号都不影响。
        IGNORE_CASE,
        // identifier 在 数据库中，不会被自动转换大小写，保持原样。
        NO_CASE
    }

    /**
     * 在DDL 中，如果 identifier 没有被 引号引用时，在数据库中应该被存储为什么形式。
     * 根据 SQL-92 标准，应该是大写形式，但并不是所有的数据库都实现了这个。
     *
     *
     * 日常写SQL时，尽量不要加引号，因为一旦加了引号，引号中的内容必须与数据库中存储的保持一致才能正确识别。
     */
    IdentifierCase unquotedIdentifierCase();

    /**
     * Database identifier: tableName, columnName, schema, keyword.
     *
     * @param identifier the name can be one of them: tableName, columnName, schemaName etc..
     * @return `identifier`
     */
    String getQuotedIdentifier(String identifier);

    String getQuotedIdentifier(String identifier, IdentifierCase identifierCase);

    String getUnquoteIdentifier(String identifier);

    /**
     * Get quote for symbol (e.g. table name, field name)
     *
     * @return the quote
     */
    char getBeforeQuote();

    /**
     * Get quote for symbol (e.g. table name, field name)
     *
     * @return the quote
     */
    char getAfterQuote();

    /**
     * Whether supports distinct keyword
     */
    boolean isSupportsDistinct();

    boolean isSupportsBatchUpdates();

    boolean isSupportsBatchSql();

    SqlCompatibilityType getDefaultSqlCompatibilityType();

    /**
     * @return 返回支持的 SQL兼容类型
     */
    List<SqlCompatibilityType> supportedSqlCompatibilityTypes();

    boolean isSupportedCompatibilityType(SqlCompatibilityType type);
}
