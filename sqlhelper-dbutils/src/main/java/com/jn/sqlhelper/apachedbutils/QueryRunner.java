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

package com.jn.sqlhelper.apachedbutils;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.io.IOs;
import com.jn.sqlhelper.apachedbutils.resultset.SelectCountResultSetHandler;
import com.jn.sqlhelper.apachedbutils.statement.setter.ArrayPreparedStatementSetter;
import com.jn.sqlhelper.apachedbutils.statement.setter.PagedPreparedStatementSetter;
import com.jn.sqlhelper.apachedbutils.statement.setter.PreparedStatementSetter;
import com.jn.sqlhelper.common.utils.SQLs;
import com.jn.sqlhelper.dialect.instrument.SQLInstrumentorConfig;
import com.jn.sqlhelper.dialect.instrument.SQLInstrumentorProvider;
import com.jn.sqlhelper.dialect.instrument.SQLStatementInstrumentor;
import com.jn.sqlhelper.dialect.pagination.*;
import com.jn.sqlhelper.dialect.parameter.ArrayBasedQueryParameters;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.StatementConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Collection;
import java.util.List;

public class QueryRunner extends org.apache.commons.dbutils.QueryRunner {
    private static final PagingRequestContextHolder PAGING_CONTEXT = PagingRequestContextHolder.getContext();
    private SQLInstrumentorConfig instrumentConfig;
    private PagingRequestBasedRowSelectionBuilder rowSelectionBuilder = new PagingRequestBasedRowSelectionBuilder();
    private static final Logger logger = LoggerFactory.getLogger(QueryRunner.class);

    private DbutilsPaginationProperties paginationConfig = new DbutilsPaginationProperties();

    /**
     * Constructor for QueryRunner.
     */
    public QueryRunner() {
        super();
    }

    /**
     * Constructor for QueryRunner that controls the use of <code>ParameterMetaData</code>.
     *
     * @param pmdKnownBroken Some drivers don't support {@link java.sql.ParameterMetaData#getParameterType(int) };
     *                       if <code>pmdKnownBroken</code> is set to true, we won't even try it; if false, we'll try it,
     *                       and if it breaks, we'll remember not to use it again.
     */
    public QueryRunner(boolean pmdKnownBroken) {
        super(pmdKnownBroken);
    }

    /**
     * Constructor for QueryRunner that takes a <code>DataSource</code> to use.
     * <p>
     * Methods that do not take a <code>Connection</code> parameter will retrieve connections from this
     * <code>DataSource</code>.
     *
     * @param ds The <code>DataSource</code> to retrieve connections from.
     */
    public QueryRunner(DataSource ds) {
        super(ds);
    }

    /**
     * Constructor for QueryRunner that takes a <code>StatementConfiguration</code> to configure statements when
     * preparing them.
     *
     * @param stmtConfig The configuration to apply to statements when they are prepared.
     */
    public QueryRunner(StatementConfiguration stmtConfig) {
        super(stmtConfig);
    }

    /**
     * Constructor for QueryRunner that takes a <code>DataSource</code> and controls the use of <code>ParameterMetaData</code>.
     * Methods that do not take a <code>Connection</code> parameter will retrieve connections from this
     * <code>DataSource</code>.
     *
     * @param ds             The <code>DataSource</code> to retrieve connections from.
     * @param pmdKnownBroken Some drivers don't support {@link java.sql.ParameterMetaData#getParameterType(int) };
     *                       if <code>pmdKnownBroken</code> is set to true, we won't even try it; if false, we'll try it,
     *                       and if it breaks, we'll remember not to use it again.
     */
    public QueryRunner(DataSource ds, boolean pmdKnownBroken) {
        super(ds, pmdKnownBroken);
    }

    /**
     * Constructor for QueryRunner that takes a <code>DataSource</code> to use and a <code>StatementConfiguration</code>.
     * <p>
     * Methods that do not take a <code>Connection</code> parameter will retrieve connections from this
     * <code>DataSource</code>.
     *
     * @param ds         The <code>DataSource</code> to retrieve connections from.
     * @param stmtConfig The configuration to apply to statements when they are prepared.
     */
    public QueryRunner(DataSource ds, StatementConfiguration stmtConfig) {
        super(ds, stmtConfig);
    }

    /**
     * Constructor for QueryRunner that takes a <code>DataSource</code>, a <code>StatementConfiguration</code>, and
     * controls the use of <code>ParameterMetaData</code>.  Methods that do not take a <code>Connection</code> parameter
     * will retrieve connections from this <code>DataSource</code>.
     *
     * @param ds             The <code>DataSource</code> to retrieve connections from.
     * @param pmdKnownBroken Some drivers don't support {@link java.sql.ParameterMetaData#getParameterType(int) };
     *                       if <code>pmdKnownBroken</code> is set to true, we won't even try it; if false, we'll try it,
     *                       and if it breaks, we'll remember not to use it again.
     * @param stmtConfig     The configuration to apply to statements when they are prepared.
     */
    public QueryRunner(DataSource ds, boolean pmdKnownBroken, StatementConfiguration stmtConfig) {
        super(ds, pmdKnownBroken, stmtConfig);
    }

    /**
     * Configuration to use when preparing statements.
     */
    private StatementConfiguration stmtConfig;

    public void setPaginationConfig(DbutilsPaginationProperties paginationConfig) {
        this.paginationConfig = paginationConfig;
    }

    public void setInstrumentConfig(SQLInstrumentorConfig instrumentConfig) {
        if (instrumentConfig == null) {
            instrumentConfig = SQLInstrumentorConfig.DEFAULT;
        }
        this.instrumentConfig = instrumentConfig;
    }


    public boolean execute(String sql) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = this.prepareConnection();
            statement = connection.createStatement();
            return statement.execute(sql);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            IOs.close(statement);
            IOs.close(connection);
        }
    }

    @Override
    public void fillStatement(PreparedStatement stmt, Object... params) throws SQLException {
        fillStatement(stmt, new ArrayPreparedStatementSetter(params), params);
    }

    public void fillStatementWithSetter(PreparedStatement stmt, PreparedStatementSetter preparedStatementSetter, Object... params) throws SQLException {
        // nothing to do here
        if (Emptys.isAnyEmpty(params)) {
            return;
        }
        if (preparedStatementSetter == null) {
            fillStatement(stmt, params);
        } else {
            preparedStatementSetter.setValues(stmt);
        }
    }

    public int executeUpdate(String sql, PreparedStatementSetter setter, Object... params) throws SQLException {
        Connection conn = this.prepareConnection();
        return this.executeUpdate(conn, true, sql, setter, params);
    }

    /**
     * Calls update after checking the parameters to ensure nothing is null.
     *
     * @param conn      The connection to use for the update call.
     * @param closeConn True if the connection should be closed, false otherwise.
     * @param sql       The SQL statement to execute.
     * @param params    An array of update replacement parameters.  Each row in
     *                  this array is one set of update replacement values.
     * @return The number of rows updated.
     * @throws SQLException If there are database or parameter errors.
     */
    private int executeUpdate(Connection conn, boolean closeConn, String sql, PreparedStatementSetter setter, Object... params) throws SQLException {
        if (conn == null) {
            throw new SQLException("Null connection");
        }

        if (sql == null) {
            if (closeConn) {
                close(conn);
            }
            throw new SQLException("Null SQL statement");
        }

        PreparedStatement stmt = null;
        int rows = 0;

        try {
            stmt = this.prepareStatement(conn, sql);
            this.fillStatementWithSetter(stmt, setter, params);
            rows = stmt.executeUpdate();

        } catch (SQLException e) {
            this.rethrow(e, sql, params);

        } finally {
            close(stmt);
            if (closeConn) {
                close(conn);
            }
        }

        return rows;
    }

    /**
     * Execute an SQL SELECT query with a single replacement parameter. The
     * caller is responsible for closing the connection.
     *
     * @param <T>   The type of object that the handler returns
     * @param conn  The connection to execute the query in.
     * @param sql   The query to execute.
     * @param param The replacement parameter.
     * @param rsh   The handler that converts the results into an object.
     * @return The object returned by the handler.
     * @throws SQLException if a database access error occurs
     */
    @Override
    public <T> T query(Connection conn, String sql, Object param, ResultSetHandler<T> rsh) throws SQLException {
        return this.<T>query(conn, false, sql, null, rsh, new Object[]{param});
    }

    /**
     * Execute an SQL SELECT query with replacement parameters.  The
     * caller is responsible for closing the connection.
     *
     * @param <T>    The type of object that the handler returns
     * @param conn   The connection to execute the query in.
     * @param sql    The query to execute.
     * @param params The replacement parameters.
     * @param rsh    The handler that converts the results into an object.
     * @return The object returned by the handler.
     * @throws SQLException if a database access error occurs
     */
    @Override
    public <T> T query(Connection conn, String sql, Object[] params, ResultSetHandler<T> rsh) throws SQLException {
        return this.<T>query(conn, false, sql, null, rsh, params);
    }

    /**
     * Execute an SQL SELECT query with replacement parameters.  The
     * caller is responsible for closing the connection.
     *
     * @param <T>    The type of object that the handler returns
     * @param conn   The connection to execute the query in.
     * @param sql    The query to execute.
     * @param rsh    The handler that converts the results into an object.
     * @param params The replacement parameters.
     * @return The object returned by the handler.
     * @throws SQLException if a database access error occurs
     */
    @Override
    public <T> T query(Connection conn, String sql, ResultSetHandler<T> rsh, Object... params) throws SQLException {
        return this.<T>query(conn, false, sql, null, rsh, params);
    }

    /**
     * Execute an SQL SELECT query without any replacement parameters.  The
     * caller is responsible for closing the connection.
     *
     * @param <T>  The type of object that the handler returns
     * @param conn The connection to execute the query in.
     * @param sql  The query to execute.
     * @param rsh  The handler that converts the results into an object.
     * @return The object returned by the handler.
     * @throws SQLException if a database access error occurs
     */
    @Override
    public <T> T query(Connection conn, String sql, ResultSetHandler<T> rsh) throws SQLException {
        return this.<T>query(conn, false, sql, null, rsh, (Object[]) null);
    }

    /**
     * Executes the given SELECT SQL with a single replacement parameter.
     * The <code>Connection</code> is retrieved from the
     * <code>DataSource</code> set in the constructor.
     *
     * @param <T>   The type of object that the handler returns
     * @param sql   The SQL statement to execute.
     * @param param The replacement parameter.
     * @param rsh   The handler used to create the result object from
     *              the <code>ResultSet</code>.
     * @return An object generated by the handler.
     * @throws SQLException if a database access error occurs
     */
    @Override
    public <T> T query(String sql, Object param, ResultSetHandler<T> rsh) throws SQLException {
        return this.<T>query(sql, rsh, new Object[]{param});
    }

    /**
     * Executes the given SELECT SQL query and returns a result object.
     * The <code>Connection</code> is retrieved from the
     * <code>DataSource</code> set in the constructor.
     *
     * @param <T>    The type of object that the handler returns
     * @param sql    The SQL statement to execute.
     * @param params Initialize the PreparedStatement's IN parameters with
     *               this array.
     * @param rsh    The handler used to create the result object from
     *               the <code>ResultSet</code>.
     * @return An object generated by the handler.
     * @throws SQLException if a database access error occurs
     */
    @Override
    public <T> T query(String sql, Object[] params, ResultSetHandler<T> rsh) throws SQLException {
        return this.<T>query(sql, rsh, params);
    }

    /**
     * Executes the given SELECT SQL query and returns a result object.
     * The <code>Connection</code> is retrieved from the
     * <code>DataSource</code> set in the constructor.
     *
     * @param <T>    The type of object that the handler returns
     * @param sql    The SQL statement to execute.
     * @param rsh    The handler used to create the result object from
     *               the <code>ResultSet</code>.
     * @param params Initialize the PreparedStatement's IN parameters with
     *               this array.
     * @return An object generated by the handler.
     * @throws SQLException if a database access error occurs
     */
    @Override
    public <T> T query(String sql, ResultSetHandler<T> rsh, Object... params) throws SQLException {
        return this.<T>query(sql, null, rsh, params);
    }

    public <T> T query(String sql, @Nullable PreparedStatementSetter setter, ResultSetHandler<T> rsh, Object... params) throws SQLException {
        Connection conn = this.prepareConnection();
        return this.<T>query(conn, true, sql, setter, rsh, params);
    }

    /**
     * Executes the given SELECT SQL without any replacement parameters.
     * The <code>Connection</code> is retrieved from the
     * <code>DataSource</code> set in the constructor.
     *
     * @param <T> The type of object that the handler returns
     * @param sql The SQL statement to execute.
     * @param rsh The handler used to create the result object from
     *            the <code>ResultSet</code>.
     * @return An object generated by the handler.
     * @throws SQLException if a database access error occurs
     */
    public <T> T query(String sql, ResultSetHandler<T> rsh) throws SQLException {
        return this.<T>query(sql, (PreparedStatementSetter) null, rsh);
    }

    public <T> T query(String sql, @Nullable PreparedStatementSetter setter, ResultSetHandler<T> rsh) throws SQLException {
        Connection conn = this.prepareConnection();

        return this.<T>query(conn, true, sql, setter, rsh, (Object[]) null);
    }


    /**
     * Calls query after checking the parameters to ensure nothing is null.
     *
     * @param conn      The connection to use for the query call.
     * @param closeConn True if the connection should be closed, false otherwise.
     * @param sql       The SQL statement to execute.
     * @param params    An array of query replacement parameters.  Each row in
     *                  this array is one set of batch replacement values.
     * @return The results of the query.
     * @throws SQLException If there are database or parameter errors.
     */
    private <T> T query(Connection conn, boolean closeConn, String sql, @Nullable PreparedStatementSetter setter, ResultSetHandler<T> rsh, Object... params)
            throws SQLException {
        if (conn == null) {
            throw new SQLException("Null connection");
        }

        if (sql == null) {
            if (closeConn) {
                close(conn);
            }
            throw new SQLException("Null SQL statement");
        }

        if (rsh == null) {
            if (closeConn) {
                close(conn);
            }
            throw new SQLException("Null ResultSetHandler");
        }

        PreparedStatement stmt = null;
        ResultSet rs = null;
        T r = null;

        try {
            if (!PAGING_CONTEXT.isPagingRequest() || !SQLs.isSelectStatement(sql) || SQLs.isSelectCountStatement(sql)) {
                stmt = this.prepareStatement(conn, sql);
                this.fillStatementWithSetter(stmt, setter, params);
                rs = this.wrap(stmt.executeQuery());
                r = rsh.handle(rs);
            } else {
                r = doPagingQuery(conn, sql, rsh, params);
            }
        } catch (SQLException e) {
            this.rethrow(e, sql, params);

        } finally {
            try {
                close(rs);
            } finally {
                close(stmt);
                if (closeConn) {
                    close(conn);
                }
            }
        }

        return r;
    }

    private class DbutilsOriginalPreparedStatementSetter implements PreparedStatementSetter {
        private Object[] params;

        DbutilsOriginalPreparedStatementSetter(Object... params) {
            this.params = params;
        }

        @Override
        public void setValues(PreparedStatement stmt) throws SQLException {
            for (int i = 1; i <= params.length; i++) {
                stmt.setObject(i, params[i - 1]);
            }
        }
    }


    private <T> T doPagingQuery(Connection conn, String sql, ResultSetHandler<T> rsh, Object... params) throws SQLException {
        final PagingRequest request = PAGING_CONTEXT.getPagingRequest();
        final PagingResult result = new PagingResult();
        request.setResult(result);
        result.setPageSize(request.getPageSize());
        List items = Collects.emptyArrayList();
        result.setPageNo(request.getPageNo());
        result.setItems(items);
        int requestPageNo = request.getPageNo();
        Object rs = null;
        if (request.isEmptyRequest()) {
            result.setTotal(0);
            rs = items;
            return (T) rs;
        }

        SQLStatementInstrumentor instrumentor = SQLInstrumentorProvider.getInstance().get(instrumentConfig);
        if (request.isGetAllRequest()) {
            String sql0 = sql;
            if (PAGING_CONTEXT.isOrderByRequest()) {
                sql0 = instrumentor.instrumentOrderBySql(sql, PAGING_CONTEXT.getPagingRequest().getOrderBy());
            }
            rs = this.query(conn, false, sql0, null, rsh, params);
            invalidatePagingRequest(false);
            if (rs == null) {
                rs = Collects.emptyArrayList();
            }
            if (rs instanceof Collection) {
                items.addAll((Collection) rs);
                result.setTotal(items.size());
            }
            return (T) rs;
        }

        try {
            if (instrumentor.beginIfSupportsLimit(conn.getMetaData())) {
                boolean needQuery = true;
                if (needCountInPagingRequest(request)) {
                    String countSql = instrumentor.countSql(sql, request.getCountColumn());
                    int count = this.query(conn, false, countSql, null, new SelectCountResultSetHandler(), params);
                    if (count <= 0) {
                        needQuery = false;
                    }
                    result.setTotal(count);
                    int maxPageCount = result.getMaxPage();
                    if (maxPageCount >= 0) {
                        if (requestPageNo > maxPageCount) {
                            if (isUseLastPageIfPageNoOut(request)) {
                                request.setPageNo(maxPageCount);
                                result.setPageNo(maxPageCount);
                            } else {
                                needQuery = false;
                            }
                        }
                    }
                } else {
                    result.setTotal(-1);
                }

                if (needQuery) {
                    applyStatementSettingsInPaginationRequest(request);
                    RowSelection rowSelection = rowSelectionBuilder.build(request);
                    String paginationSql = sql;
                    boolean subqueryPagination = false;
                    if (SqlPaginations.isSubqueryPagingRequest(request)) {
                        if (!SqlPaginations.isValidSubQueryPagination(request, instrumentor)) {
                            logger.warn("Paging request is not a valid subquery pagination request, so the paging request will not as a subquery pagination request. request: {}, the instrument configuration is: {}", request, instrumentor.getConfig());
                        } else {
                            subqueryPagination = true;
                        }
                    }

                    int beforeSubqueryParametersCount = 0;
                    int afterSubqueryParametersCount = 0;

                    if (!subqueryPagination) {
                        if (PAGING_CONTEXT.isOrderByRequest()) {
                            paginationSql = instrumentor.instrumentOrderByLimitSql(sql, PAGING_CONTEXT.getPagingRequest().getOrderBy(), rowSelection);
                        } else {
                            paginationSql = instrumentor.instrumentLimitSql(sql, rowSelection);
                        }
                    } else {
                        String startFlag = SqlPaginations.getSubqueryPaginationStartFlag(request, instrumentor);
                        String endFlag = SqlPaginations.getSubqueryPaginationEndFlag(request, instrumentor);
                        String subqueryPartition = SqlPaginations.extractSubqueryPartition(sql, startFlag, endFlag);
                        if (Strings.isEmpty(subqueryPartition)) {
                            throw new IllegalArgumentException("Your pagination sql is wrong, maybe used start flag or end flag is wrong");
                        }
                        String limitedSubqueryPartition = instrumentor.instrumentLimitSql(subqueryPartition, rowSelection);
                        String beforeSubqueryPartition = SqlPaginations.extractBeforeSubqueryPartition(sql, startFlag);
                        String afterSubqueryPartition = SqlPaginations.extractAfterSubqueryPartition(sql, endFlag);
                        paginationSql = beforeSubqueryPartition + " " + limitedSubqueryPartition + " " + afterSubqueryPartition;
                        if (PAGING_CONTEXT.isOrderByRequest()) {
                            paginationSql = instrumentor.instrumentOrderBySql(paginationSql, PAGING_CONTEXT.getPagingRequest().getOrderBy());
                        }

                        beforeSubqueryParametersCount = SqlPaginations.findPlaceholderParameterCount(beforeSubqueryPartition);
                        afterSubqueryParametersCount = SqlPaginations.findPlaceholderParameterCount(afterSubqueryPartition);

                    }


                    PreparedStatement ps = new PagedPreparedStatement(this.prepareStatement(conn, paginationSql));

                    ArrayBasedQueryParameters queryParameters = new ArrayBasedQueryParameters();
                    queryParameters.setCallable(false);
                    queryParameters.setRowSelection(rowSelection);
                    queryParameters.setParameters(params, beforeSubqueryParametersCount, afterSubqueryParametersCount);

                    PagedPreparedStatementSetter parameterSetter = new PagedPreparedStatementSetter(new DbutilsOriginalPreparedStatementSetter(params));
                    instrumentor.bindParameters(ps, parameterSetter, queryParameters, true);
                    // execute
                    ResultSet resultSet = this.wrap(ps.executeQuery());
                    items.addAll((List) rsh.handle(resultSet));
                }
                request.setPageNo(requestPageNo);
                result.setPageNo(request.getPageNo());
                rs = items;
            } else {
                return this.query(conn, false, sql, null, rsh, params);
            }
        } finally {
            instrumentor.finish();
        }
        return (T) rs;
    }

    private void invalidatePagingRequest(boolean force) {
        PagingRequest request = PAGING_CONTEXT.getPagingRequest();
        if (request != null) {
            request.clear(force);
        }
        PAGING_CONTEXT.remove();
    }

    private boolean needCountInPagingRequest(PagingRequest request) {
        if (request.needCount() == null) {
            return paginationConfig.isCount();
        }
        if (Boolean.TRUE.equals(request.needCount())) {
            return !SqlPaginations.isSubqueryPagingRequest(request);
        }
        return false;
    }

    private boolean isUseLastPageIfPageNoOut(@NonNull PagingRequest request) {
        Preconditions.checkNotNull(request);
        if (request.isUseLastPageIfPageOut() == null) {
            return paginationConfig.isUseLastPageIfPageOut();
        }
        return request.isUseLastPageIfPageOut();
    }

    private void applyStatementSettingsInPaginationRequest(PagingRequest pagingRequest) throws SQLException {

        if (stmtConfig != null) {

            if (stmtConfig.isFetchSizeSet()) {
                pagingRequest.setFetchSize(stmtConfig.getFetchSize());
            }

            if (stmtConfig.isMaxRowsSet()) {
                pagingRequest.setMaxRows(stmtConfig.getMaxRows());
            }

            if (stmtConfig.isQueryTimeoutSet()) {
                pagingRequest.setTimeout(stmtConfig.getQueryTimeout());
            }
        }
    }

    private void configureStatement(Statement stmt) throws SQLException {
        if (stmtConfig != null) {
            if (stmtConfig.isFetchDirectionSet()) {
                stmt.setFetchDirection(stmtConfig.getFetchDirection());
            }

            if (stmtConfig.isFetchSizeSet()) {
                stmt.setFetchSize(stmtConfig.getFetchSize());
            }

            if (stmtConfig.isMaxFieldSizeSet()) {
                stmt.setMaxFieldSize(stmtConfig.getMaxFieldSize());
            }

            if (stmtConfig.isMaxRowsSet()) {
                stmt.setMaxRows(stmtConfig.getMaxRows());
            }

            if (stmtConfig.isQueryTimeoutSet()) {
                stmt.setQueryTimeout(stmtConfig.getQueryTimeout());
            }
        }
    }


}
