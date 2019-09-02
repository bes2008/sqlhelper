package com.jn.sqlhelper.springjdbc;

import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.sqlhelper.dialect.RowSelection;
import com.jn.sqlhelper.dialect.SQLInstrumentorProvider;
import com.jn.sqlhelper.dialect.SQLStatementInstrumentor;
import com.jn.sqlhelper.dialect.SQLs;
import com.jn.sqlhelper.dialect.pagination.PagingRequest;
import com.jn.sqlhelper.dialect.pagination.PagingRequestContext;
import com.jn.sqlhelper.dialect.pagination.PagingRequestContextHolder;
import com.jn.sqlhelper.dialect.pagination.PagingResult;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class JdbcTemplate extends org.springframework.jdbc.core.JdbcTemplate {
    private static final PagingRequestContextHolder<PagingRequestContext> PAGING_CONTEXT = (PagingRequestContextHolder<PagingRequestContext>) PagingRequestContextHolder.getContext();

    /**
     * supports for under 5.0
     *
     * @return DataSource
     */
    protected DataSource obtainDataSource() {
        DataSource dataSource = getDataSource();
        Preconditions.checkNotNull(dataSource, "No DataSource set");
        return dataSource;
    }

    public <T> T query(String sql, PreparedStatementSetter pss0, final ResultSetExtractor<T> rse) throws DataAccessException {
        if (!PAGING_CONTEXT.isPagingRequest() || !SQLs.isSelectStatement(sql)) {
            return originalQuery(sql, pss0, rse);
        } else {
            Assert.notNull(rse, "ResultSetExtractor must not be null");
            logger.debug("Executing prepared SQL query");

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
                rs = originalQuery(sql0, pss0, rse);
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


            Connection conn = DataSourceUtils.getConnection(obtainDataSource());


            Preconditions.checkNotNull(instrumentor);
            try {
                if (instrumentor.beginIfSupportsLimit(conn.getMetaData())) {
                    boolean needQuery = true;
                    if (request.needCount() == Boolean.TRUE) {
                        String countSql = instrumentor.countSql(sql);
                        int count = originalQuery(countSql, pss0, new ResultSetExtractor<Integer>() {
                            @Override
                            public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                                if (rs.first()) {
                                    return rs.getInt(1);
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
                                request.setPageNo(maxPageCount);
                                result.setPageNo(maxPageCount);
                            }
                        }
                    }

                    if (needQuery) {
                        RowSelection rowSelection = null;
                        String paginationSql = PAGING_CONTEXT.isOrderByRequest() ? instrumentor.instrumentOrderByLimitSql(sql, request.getOrderBy(), rowSelection) : instrumentor.instrumentLimitSql(sql, rowSelection);
                        PreparedStatement ps = null;
                        ps = new PaginationPreparedStatement(new SimplePreparedStatementCreator(paginationSql).createPreparedStatement(conn));

                        SpringJdbcQueryParameters queryParameters = new SpringJdbcQueryParameters();
                        queryParameters.setCallable(false);
                        queryParameters.setRowSelection(rowSelection);

                        instrumentor.bindParameters(ps, new PaginationPreparedStatementSetter(pss0), queryParameters, true);
                        // DO execute
                    }
                } else {
                    return originalQuery(sql, pss0, rse);
                }
            } catch (SQLException ex) {

            } finally {
                instrumentor.finish();
            }
            PreparedStatementCreator psc = null;//new PaginationPreparedStatementCreator(this, sql);
            final PreparedStatementSetter pss = null;//new PaginationPreparedStatementSetter(pss0);

            return execute(psc, new PreparedStatementCallback<T>() {
                @Override
                @Nullable
                public T doInPreparedStatement(PreparedStatement ps) throws SQLException {
                    ResultSet rs = null;
                    try {
                        if (pss != null) {
                            pss.setValues(ps);
                        }
                        rs = ps.executeQuery();
                        return rse.extractData(rs);
                    } finally {
                        JdbcUtils.closeResultSet(rs);
                        if (pss instanceof ParameterDisposer) {
                            ((ParameterDisposer) pss).cleanupParameters();
                        }
                    }
                }
            });
        }
    }

    private void invalidatePagingRequest(boolean force) {
        PagingRequest request = PAGING_CONTEXT.getPagingRequest();
        if (request != null) {
            request.clear(force);
        }
        PAGING_CONTEXT.remove();
    }

    private <T> T originalQuery(String sql, PreparedStatementSetter pss, final ResultSetExtractor<T> rse) throws DataAccessException {
        return query(new SimplePreparedStatementCreator(sql), pss, rse);
    }
}
