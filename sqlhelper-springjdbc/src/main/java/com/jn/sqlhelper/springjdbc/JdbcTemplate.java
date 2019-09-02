package com.jn.sqlhelper.springjdbc;

import com.jn.sqlhelper.dialect.SQLs;
import com.jn.sqlhelper.dialect.pagination.PagingRequestContext;
import com.jn.sqlhelper.dialect.pagination.PagingRequestContextHolder;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcTemplate extends org.springframework.jdbc.core.JdbcTemplate {
    private static final PagingRequestContextHolder<PagingRequestContext> PAGING_CONTEXT = (PagingRequestContextHolder<PagingRequestContext>) PagingRequestContextHolder.getContext();

    public <T> T query(String sql, PreparedStatementSetter pss0, final ResultSetExtractor<T> rse) throws DataAccessException {
        if (!PAGING_CONTEXT.isPagingRequest() || !SQLs.isSelectStatement(sql)) {
            return query(new SimplePreparedStatementCreator(sql), pss0, rse);
        } else {
            Assert.notNull(rse, "ResultSetExtractor must not be null");
            logger.debug("Executing prepared SQL query");
            PreparedStatementCreator psc = new PaginationPreparedStatementCreator(this, sql);
            final PreparedStatementSetter pss = new PaginationPreparedStatementSetter(pss0);

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
}
