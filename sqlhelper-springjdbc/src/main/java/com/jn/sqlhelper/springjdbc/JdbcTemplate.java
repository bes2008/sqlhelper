package com.jn.sqlhelper.springjdbc;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.sqlhelper.common.utils.SQLs;
import com.jn.sqlhelper.dialect.RowSelection;
import com.jn.sqlhelper.dialect.SQLInstrumentorProvider;
import com.jn.sqlhelper.dialect.SQLStatementInstrumentor;
import com.jn.sqlhelper.dialect.conf.SQLInstrumentConfig;
import com.jn.sqlhelper.dialect.pagination.*;
import com.jn.sqlhelper.springjdbc.statement.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class JdbcTemplate extends org.springframework.jdbc.core.JdbcTemplate {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcTemplate.class);
    private static final PagingRequestContextHolder PAGING_CONTEXT = PagingRequestContextHolder.getContext();
    private PagingRequestBasedRowSelectionBuilder rowSelectionBuilder = new PagingRequestBasedRowSelectionBuilder();

    private JdbcTemplatePaginationProperties paginationConfig = new JdbcTemplatePaginationProperties();
    private SQLInstrumentConfig instrumentConfig;

    public JdbcTemplate() {
        super();
    }

    /**
     * Construct a new JdbcTemplate, given a DataSource to obtain connections from.
     * <p>Note: This will not trigger initialization of the exception translator.
     *
     * @param dataSource the JDBC DataSource to obtain connections from
     */
    public JdbcTemplate(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * Construct a new JdbcTemplate, given a DataSource to obtain connections from.
     * <p>Note: Depending on the "lazyInit" flag, initialization of the exception translator
     * will be triggered.
     *
     * @param dataSource the JDBC DataSource to obtain connections from
     * @param lazyInit   whether to lazily initialize the SQLExceptionTranslator
     */
    public JdbcTemplate(DataSource dataSource, boolean lazyInit) {
        super(dataSource, lazyInit);
    }

    public void setPaginationConfig(JdbcTemplatePaginationProperties paginationConfig) {
        this.paginationConfig = paginationConfig;
    }

    public void setInstrumentConfig(SQLInstrumentConfig instrumentConfig) {
        if (instrumentConfig == null) {
            instrumentConfig = SQLInstrumentConfig.DEFAULT;
        }
        this.instrumentConfig = instrumentConfig;
    }

    /**
     * supports for under 5.0
     *
     * @return DataSource
     */
    protected DataSource dataSource() {
        DataSource dataSource = getDataSource();
        Preconditions.checkNotNull(dataSource, "No DataSource set");
        return dataSource;
    }


    @Override
    public <T> T query(final String sql, final ResultSetExtractor<T> rse) throws DataAccessException {
        if (!PAGING_CONTEXT.isPagingRequest() || !SQLs.isSelectStatement(sql)) {
            return super.query(sql, rse);
        } else {
            Preconditions.checkNotNull(rse, "ResultSetExtractor must not be null");
            if (logger.isDebugEnabled()) {
                logger.debug("Executing prepared SQL query");
            }

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
                rs = super.query(sql0, rse);
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


            Connection conn = DataSourceUtils.getConnection(dataSource());

            Preconditions.checkNotNull(instrumentor);
            try {
                if (instrumentor.beginIfSupportsLimit(conn.getMetaData())) {
                    boolean needQuery = true;
                    if (needCountInPagingRequest(request)) {
                        String countSql = instrumentor.countSql(sql, request.getCountColumn());
                        int count = super.query(countSql, new ResultSetExtractor<Integer>() {
                            @Override
                            public Integer extractData(ResultSet rs0) throws SQLException, DataAccessException {
                                if (rs0.next() && rs0.getMetaData().getColumnCount() > 0) {
                                    return rs0.getInt(1);
                                } else {
                                    return 0;
                                }
                            }
                        });
                        if (count <= 0) {
                            needQuery = false;
                        }
                        result.setTotal(count);
                        int maxPageCount = result.getMaxPageCount();
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
                        String paginationSql = PAGING_CONTEXT.isOrderByRequest() ? instrumentor.instrumentOrderByLimitSql(sql, request.getOrderBy(), rowSelection) : instrumentor.instrumentLimitSql(sql, rowSelection);
                        PreparedStatement ps = new PaginationPreparedStatement(new SimplePreparedStatementCreator(paginationSql).createPreparedStatement(conn));

                        SpringJdbcQueryParameters queryParameters = new SpringJdbcQueryParameters();
                        queryParameters.setCallable(false);
                        queryParameters.setRowSelection(rowSelection);

                        instrumentor.bindParameters(ps, new PaginationPreparedStatementSetter(null), queryParameters, true);
                        // DO execute
                        ResultSet resultSet = null;
                        try {
                            resultSet = ps.executeQuery();
                            List rows = (List) rse.extractData(resultSet);
                            items.addAll(rows);
                        } finally {
                            JdbcUtils.closeResultSet(resultSet);
                        }
                        handleWarnings(ps);
                    }

                    request.setPageNo(requestPageNo);
                    result.setPageNo(request.getPageNo());
                    rs = items;
                } else {
                    return super.query(sql, rse);
                }
            } catch (SQLException ex) {
                throw translateException("PreparedStatementCallback", sql, ex);
            } finally {
                instrumentor.finish();
            }
            return (T) rs;
        }
    }


    @Override
    public <T> T query(PreparedStatementCreator psc, PreparedStatementSetter pss, final ResultSetExtractor<T> rse) throws DataAccessException {
        if (!(psc instanceof SqlProvider)) {
            return super.query(psc, pss, rse);
        }

        final String sql = ((SqlProvider) psc).getSql();

        if (!PAGING_CONTEXT.isPagingRequest() || !SQLs.isSelectStatement(sql)) {
            return super.query(psc, pss, rse);
        } else {
            Preconditions.checkNotNull(rse, "ResultSetExtractor must not be null");
            if (logger.isDebugEnabled()) {
                logger.debug("Executing prepared SQL query");
            }

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
            SQLStatementInstrumentor instrumentor = SQLInstrumentorProvider.getInstance().get();
            if (request.isGetAllRequest()) {
                String sql0 = sql;
                if (PAGING_CONTEXT.isOrderByRequest()) {
                    sql0 = instrumentor.instrumentOrderBySql(sql, PAGING_CONTEXT.getPagingRequest().getOrderBy());
                }
                rs = super.query(new SimplePreparedStatementCreator(sql0), pss, rse);
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

            Connection conn = DataSourceUtils.getConnection(dataSource());
            try {
                if (instrumentor.beginIfSupportsLimit(conn.getMetaData())) {
                    boolean needQuery = true;
                    if (needCountInPagingRequest(request)) {
                        String countSql = instrumentor.countSql(sql, request.getCountColumn());
                        int count = super.query(new SimplePreparedStatementCreator(countSql), pss == null && (psc instanceof NamedParameterPreparedStatementCreator) ? (NamedParameterPreparedStatementCreator) psc : pss, new ResultSetExtractor<Integer>() {
                            @Override
                            public Integer extractData(ResultSet rs0) throws SQLException, DataAccessException {
                                if (rs0.next() && rs0.getMetaData().getColumnCount() > 0) {
                                    return rs0.getInt(1);
                                } else {
                                    return 0;
                                }
                            }
                        });
                        if (count <= 0) {
                            needQuery = false;
                        }
                        result.setTotal(count);
                        int maxPageCount = result.getMaxPageCount();
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
                        boolean subQueryPagination = false;
                        if (SqlPaginations.isSubqueryPagingRequest(request)) {
                            if (!SqlPaginations.isValidSubQueryPagination(request, instrumentor)) {
                                LOGGER.warn("Paging request is not a valid subquery pagination request, so the paging request will not as a subquery pagination request. request: {}, the instrument configuration is: {}", request, instrumentor.getConfig());
                            } else {
                                subQueryPagination = true;
                            }
                        }

                        int beforeSubqueryParametersCount = 0;
                        int afterSubqueryParametersCount = 0;

                        if (!subQueryPagination) {
                            if (PAGING_CONTEXT.isOrderByRequest()) {
                                paginationSql = instrumentor.instrumentOrderByLimitSql(sql, PAGING_CONTEXT.getPagingRequest().getOrderBy(), rowSelection);
                            } else {
                                paginationSql = instrumentor.instrumentLimitSql(sql, rowSelection);
                            }
                            PagingRequestContext ctx = PAGING_CONTEXT.get();
                        } else {
                            String startFlag = SqlPaginations.getSubqueryPaginationStartFlag(request, instrumentor);
                            String endFlag = SqlPaginations.getSubqueryPaginationEndFlag(request, instrumentor);
                            String subqueryPartition = SqlPaginations.extractSubqueryPartition(sql, startFlag, endFlag);
                            String limitedSubqueryPartition = instrumentor.instrumentLimitSql(subqueryPartition, rowSelection);
                            String beforeSubqueryPartition = SqlPaginations.extractBeforeSubqueryPartition(sql, startFlag);
                            String afterSubqueryPartition = SqlPaginations.extractAfterSubqueryPartition(sql, endFlag);
                            paginationSql = beforeSubqueryPartition + " " + limitedSubqueryPartition + " " + afterSubqueryPartition;
                            if (PAGING_CONTEXT.isOrderByRequest()) {
                                paginationSql = instrumentor.instrumentOrderBySql(paginationSql, PAGING_CONTEXT.getPagingRequest().getOrderBy());
                            }

                            PagingRequestContext ctx = PAGING_CONTEXT.get();
                            beforeSubqueryParametersCount = SqlPaginations.findPlaceholderParameterCount(beforeSubqueryPartition);
                            afterSubqueryParametersCount = SqlPaginations.findPlaceholderParameterCount(afterSubqueryPartition);
                        }

                        if (psc instanceof NamedParameterPreparedStatementCreator) {
                            NamedParameterPreparedStatementCreator oldCreator = (NamedParameterPreparedStatementCreator) psc;
                            psc = new NamedParameterPreparedStatementCreator(paginationSql, oldCreator.getParameters(), oldCreator.getFactory());
                        } else {
                            psc = new SimplePreparedStatementCreator(paginationSql);
                        }
                        PreparedStatement ps = new PaginationPreparedStatement(psc.createPreparedStatement(conn));

                        SpringJdbcQueryParameters queryParameters = new SpringJdbcQueryParameters();
                        queryParameters.setCallable(false);
                        queryParameters.setRowSelection(rowSelection);
                        queryParameters.setParameters(null, beforeSubqueryParametersCount, afterSubqueryParametersCount);


                        PaginationPreparedStatementSetter parameterSetter = (pss == null && psc instanceof NamedParameterPreparedStatementCreator) ? new PaginationPreparedStatementSetter((NamedParameterPreparedStatementCreator) psc) : new PaginationPreparedStatementSetter(pss);
                        instrumentor.bindParameters(ps, parameterSetter, queryParameters, true);
                        // DO execute
                        ResultSet resultSet = null;
                        try {
                            resultSet = ps.executeQuery();
                            List rows = (List) rse.extractData(resultSet);
                            items.addAll(rows);
                        } finally {
                            JdbcUtils.closeResultSet(resultSet);
                            if (pss instanceof ParameterDisposer) {
                                ((ParameterDisposer) pss).cleanupParameters();
                            }
                        }
                        handleWarnings(ps);
                    }

                    request.setPageNo(requestPageNo);
                    result.setPageNo(request.getPageNo());
                    rs = items;
                } else {
                    return super.query(new SimplePreparedStatementCreator(sql), pss, rse);
                }
            } catch (SQLException ex) {
                throw translateException("PreparedStatementCallback", sql, ex);
            } finally {
                instrumentor.finish();
            }
            return (T) rs;
        }
    }

    /**
     * for Spring 4.x
     */
    protected DataAccessException translateException(String task, String sql, SQLException ex) {
        DataAccessException dae = getExceptionTranslator().translate(task, sql, ex);
        return (dae != null ? dae : new UncategorizedSQLException(task, sql, ex));
    }

    private void applyStatementSettingsInPaginationRequest(PagingRequest pagingRequest) throws SQLException {
        int fetchSize = getFetchSize();
        if (fetchSize > -1) {
            pagingRequest.setFetchSize(fetchSize);
        }
        int maxRows = getMaxRows();
        if (maxRows > -1) {
            pagingRequest.setMaxRows(maxRows);
        }
        if (getQueryTimeout() > -1) {
            pagingRequest.setTimeout(getQueryTimeout());
        }
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
        if( Boolean.TRUE.equals(request.needCount())){
            return !SqlPaginations.isSubqueryPagingRequest(request);
        }
        return false;
    }

    private boolean isUseLastPageIfPageNoOut(@NonNull PagingRequest request) {
        Preconditions.checkNotNull(request);
        if (request.isUseLastPageIfPageNoOut() == null) {
            return paginationConfig.isUseLastPageIfPageNoOut();
        }
        return request.isUseLastPageIfPageNoOut();
    }
}
