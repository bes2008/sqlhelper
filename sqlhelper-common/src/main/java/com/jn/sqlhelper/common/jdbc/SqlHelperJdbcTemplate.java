package com.jn.sqlhelper.common.jdbc;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.io.IOs;
import com.jn.sqlhelper.common.resultset.ResultSetExtractor;
import com.jn.sqlhelper.common.resultset.RowMapper;
import com.jn.sqlhelper.common.resultset.RowMapperResultSetExtractor;
import com.jn.sqlhelper.common.resultset.SingleRecordRowMapperResultSetExtractor;
import com.jn.sqlhelper.common.statement.CallableStatementSetter;
import com.jn.sqlhelper.common.statement.ListPreparedStatementSetter;
import com.jn.sqlhelper.common.statement.PreparedStatementSetter;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class SqlHelperJdbcTemplate implements JdbcTemplate{

    private DataSource dataSource;

    public SqlHelperJdbcTemplate(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Executes this query with these parameters against this connection.
     *
     * @param sql The sql to execute.
     * @return The query results.
     * @throws SQLException when the query execution failed.
     */
    public boolean execute(String sql) throws SQLException {
        Statement statement = null;
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            statement = conn.createStatement();
            return statement.execute(sql);
        } finally {
            IOs.close(statement);
            IOs.close(conn);
        }
    }

    /**
     * Executes this query with these parameters against this connection.
     *
     * @param sql    The sql to execute.
     * @param params The query parameters.
     * @return The query results.
     * @throws SQLException when the query execution failed.
     */
    public <T> T executeQuery(String sql, PreparedStatementSetter preparedStatementSetter, ResultSetExtractor<T> extractor, Object... params) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Connection conn = null;
        T result;
        try {
            conn = dataSource.getConnection();
            statement = conn.prepareStatement(sql);
            preparedStatementSetter.setParameters(statement, 1, params);
            resultSet = statement.executeQuery();
            result = extractor.extract(resultSet);
        } finally {
            IOs.close(resultSet);
            IOs.close(statement);
            IOs.close(conn);
        }
        return result;
    }

    public <T> T executeQuery(String sql, ResultSetExtractor<T> extractor, Object... params) throws SQLException {
        return executeQuery(sql, new ListPreparedStatementSetter(), extractor, Collects.asList(params));
    }

    public <T> List<T> queryList(String sql, RowMapper<T> rowMapper, Object... params) throws SQLException {
        ResultSetExtractor<List<T>> extractor = new RowMapperResultSetExtractor<T>(rowMapper);
        List<T> result = executeQuery(sql, extractor, params);
        return result;
    }

    public <T> T queryOne(String sql, RowMapper<T> rowMapper, Object... params) throws SQLException {
        ResultSetExtractor<T> extractor = new SingleRecordRowMapperResultSetExtractor<T>(rowMapper);
        T result = executeQuery(sql, extractor, params);
        return result;
    }

    public int executeUpdate(String sql, PreparedStatementSetter preparedStatementSetter, Object... params) throws SQLException {
        PreparedStatement statement = null;
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            statement = conn.prepareStatement(sql);
            preparedStatementSetter.setParameters(statement, 1, params);
            return statement.executeUpdate();
        } finally {
            IOs.close(statement);
            IOs.close(conn);
        }
    }

    public <T> T call(String sql, List<?> params, ResultSetExtractor<T> extractor) throws SQLException {
        CallableStatement statement = null;
        Connection conn = null;
        ResultSet resultSet = null;
        T result;
        try {
            conn = dataSource.getConnection();
            statement = conn.prepareCall(sql);
            CallableStatementSetter callableStatementSetter = new CallableStatementSetter();
            callableStatementSetter.setParameters(statement, 1, params);
            resultSet = statement.executeQuery();
            result = extractor.extract(resultSet);
        } finally {
            IOs.close(resultSet);
            IOs.close(statement);
            IOs.close(conn);
        }
        return result;
    }

    private void batchUpdate() {
        Connection conn;
        // will DO
    }

}
