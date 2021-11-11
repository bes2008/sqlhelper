package com.jn.sqlhelper.springjdbc;

import com.jn.sqlhelper.common.jdbc.JdbcTemplate;
import com.jn.sqlhelper.common.resultset.ResultSetExtractor;
import com.jn.sqlhelper.common.resultset.RowMapper;
import com.jn.sqlhelper.common.statement.PreparedStatementSetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.SqlProvider;
import org.springframework.jdbc.core.StatementCallback;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class SpringJdbcTemplateAdapter implements JdbcTemplate {
    private static final Logger logger = LoggerFactory.getLogger(SpringJdbcTemplateAdapter.class);
    org.springframework.jdbc.core.JdbcTemplate delegate;

    public SpringJdbcTemplateAdapter(org.springframework.jdbc.core.JdbcTemplate delegate) {
        this.delegate = delegate;
    }

    @Override
    public DataSource getDataSource() {
        return delegate.getDataSource();
    }

    @Override
    public boolean execute(final String sql) throws SQLException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Executing SQL statement [" + sql + "]");
        }
        class ExecuteStatementCallback implements StatementCallback<Boolean>, SqlProvider {
            ExecuteStatementCallback() {
            }

            public Boolean doInStatement(Statement stmt) throws SQLException {
                return stmt.execute(sql);
            }

            public String getSql() {
                return sql;
            }
        }

        return delegate.execute(new ExecuteStatementCallback());
    }

    @Override
    public <T> T executeQuery(String sql, PreparedStatementSetter preparedStatementSetter, ResultSetExtractor<T> extractor, Object... params) throws SQLException {
        //

        return null;
    }

    @Override
    public <T> T executeQuery(String sql, ResultSetExtractor<T> extractor, Object... params) throws SQLException {
        return null;
    }

    @Override
    public <T> List<T> queryList(String sql, RowMapper<T> rowMapper, Object... params) throws SQLException {
        return null;
    }

    @Override
    public <T> T queryOne(String sql, RowMapper<T> rowMapper, Object... params) throws SQLException {
        return null;
    }

    @Override
    public int executeUpdate(String sql, PreparedStatementSetter preparedStatementSetter, Object... params) throws SQLException {
        return 0;
    }

    @Override
    public <T> T call(String sql, List<?> params, ResultSetExtractor<T> extractor) throws SQLException {
        return null;
    }
}
