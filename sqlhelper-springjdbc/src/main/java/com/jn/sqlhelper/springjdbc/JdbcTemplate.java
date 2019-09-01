package com.jn.sqlhelper.springjdbc;

import com.jn.sqlhelper.dialect.SQLs;
import com.jn.sqlhelper.dialect.pagination.PagingRequestContext;
import com.jn.sqlhelper.dialect.pagination.PagingRequestContextHolder;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.sql.DataSource;

public class JdbcTemplate extends org.springframework.jdbc.core.JdbcTemplate {
    private static final PagingRequestContextHolder<PagingRequestContext> PAGING_CONTEXT = (PagingRequestContextHolder<PagingRequestContext>) PagingRequestContextHolder.getContext();

    public <T> T query(String sql, PreparedStatementSetter pss, ResultSetExtractor<T> rse) throws DataAccessException {
        if (!PAGING_CONTEXT.isPagingRequest() || !SQLs.isSelectStatement(sql)) {
            return query(new SimplePreparedStatementCreator(sql), pss, rse);
        } else {
            return query(new PaginationPreparedStatementCreator(this, sql), pss, rse);
        }
    }
}
