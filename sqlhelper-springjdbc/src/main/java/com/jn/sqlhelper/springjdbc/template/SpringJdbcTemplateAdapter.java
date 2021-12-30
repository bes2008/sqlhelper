package com.jn.sqlhelper.springjdbc.template;

import com.jn.langx.util.Objs;
import com.jn.langx.util.logging.Loggers;
import com.jn.sqlhelper.common.jdbc.JdbcTemplate;
import com.jn.sqlhelper.common.resultset.ResultSetExtractor;
import com.jn.sqlhelper.common.resultset.RowMapper;
import com.jn.sqlhelper.common.statement.PreparedStatementSetter;
import com.jn.sqlhelper.springjdbc.resultset.ResultSetExtractorAdaptor;
import com.jn.sqlhelper.springjdbc.resultset.RowMapperAdapter;
import com.jn.sqlhelper.springjdbc.statement.ArgumentPreparedStatementSetter;
import com.jn.sqlhelper.springjdbc.statement.PreparedStatementSetterAdapter;
import org.slf4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.SqlProvider;
import org.springframework.jdbc.core.StatementCallback;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class SpringJdbcTemplateAdapter implements JdbcTemplate {
    private static final Logger logger = Loggers.getLogger(SpringJdbcTemplateAdapter.class);
    private org.springframework.jdbc.core.JdbcTemplate delegate;

    public SpringJdbcTemplateAdapter(org.springframework.jdbc.core.JdbcTemplate delegate) {
        this.delegate = delegate;
    }

    @Override
    public DataSource getDataSource() {
        return delegate.getDataSource();
    }

    @Override
    public boolean execute(final String sql) throws SQLException {
        if (logger.isDebugEnabled()) {
            logger.debug("Executing SQL statement: {}", sql);
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
        return this.delegate.query(sql, new PreparedStatementSetterAdapter(preparedStatementSetter, params), new ResultSetExtractorAdaptor<T, ResultSetExtractor<T>>(extractor));
    }

    @Override
    public <T> T executeQuery(String sql, ResultSetExtractor<T> extractor, Object... params) throws SQLException {
        return this.delegate.query(sql, new ArgumentPreparedStatementSetter(params), new ResultSetExtractorAdaptor<T, ResultSetExtractor<T>>(extractor));
    }

    @Override
    public <T> List<T> queryList(String sql, RowMapper<T> rowMapper, Object... params) throws SQLException {
        return this.delegate.query(sql, new RowMapperAdapter<T>(rowMapper), params);
    }

    @Override
    public <T> T queryOne(String sql, RowMapper<T> rowMapper, Object... params) throws SQLException {
        List<T> list = this.delegate.query(sql, new RowMapperAdapter<T>(rowMapper), params);
        if (Objs.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public int executeUpdate(String sql, PreparedStatementSetter preparedStatementSetter, Object... params) throws SQLException {
        return this.delegate.update(sql, new PreparedStatementSetterAdapter(preparedStatementSetter,params));
    }

    @Override
    public <T> T call(final String sql, List<?> params, ResultSetExtractor<T> extractor) throws SQLException {
        return this.delegate.execute(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection conn) throws SQLException {
                CallableStatement statement = conn.prepareCall(sql);

                return statement;
            }
        }, new CallableStatementCallback<T>() {
            @Override
            public T doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                return null;
            }
        });
    }
}
