package com.jn.sqlhelper.springjdbc;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Preconditions;
import com.jn.sqlhelper.dialect.RowSelection;
import com.jn.sqlhelper.dialect.SQLInstrumentorProvider;
import com.jn.sqlhelper.dialect.SQLStatementInstrumentor;
import com.jn.sqlhelper.dialect.pagination.PagingRequest;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SqlProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PaginationPreparedStatementCreator implements PreparedStatementCreator, SqlProvider {
    private SimplePreparedStatementCreator delegate;
    private JdbcTemplate jdbcTemplate;
    private final String originalSql;
    private String preparedSql;

    public PaginationPreparedStatementCreator(JdbcTemplate jdbcTemplate, String sql) {
        Preconditions.checkNotNull(sql, "SQL must not be null");
        this.originalSql = sql;
        this.jdbcTemplate = jdbcTemplate;
        delegate = new SimplePreparedStatementCreator(sql);
    }

    @NonNull
    @Override
    public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {


        SQLStatementInstrumentor instrumentor = SQLInstrumentorProvider.getInstance().get();
        Preconditions.checkNotNull(instrumentor);
        try {
            if (instrumentor.beginIfSupportsLimit(conn.getMetaData())) {
                RowSelection rowSelection = null;
                this.preparedSql = SQLInstrumentorProvider.getInstance().get().instrumentLimitSql(originalSql, rowSelection);
                return conn.prepareStatement(this.preparedSql);
            } else {
                return delegate.createPreparedStatement(conn);
            }
        } finally {
            instrumentor.finish();
        }
    }

    @Override
    public String getSql() {
        return preparedSql == null ? this.originalSql : this.preparedSql;
    }

    @Override
    public String toString() {
        return StringTemplates.format("original sql : {0} \n prepared pagination sql: {1}", originalSql, preparedSql);
    }
}