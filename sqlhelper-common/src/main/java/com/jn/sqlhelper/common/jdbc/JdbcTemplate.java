package com.jn.sqlhelper.common.jdbc;

import com.jn.sqlhelper.common.resultset.ResultSetExtractor;
import com.jn.sqlhelper.common.resultset.RowMapper;
import com.jn.sqlhelper.common.statement.PreparedStatementSetter;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/**
 * 所有的方法执行完毕，都会自动的调用 Connection#close()方法
 */
public interface JdbcTemplate {
    DataSource getDataSource();

    boolean execute(String sql) throws SQLException;

    <T> T executeQuery(String sql, PreparedStatementSetter preparedStatementSetter, ResultSetExtractor<T> extractor, Object... params) throws SQLException;

    <T> T executeQuery(String sql, ResultSetExtractor<T> extractor, Object... params) throws SQLException;

    <T> List<T> queryList(String sql, RowMapper<T> rowMapper, Object... params) throws SQLException;

    <T> T queryOne(String sql, RowMapper<T> rowMapper, Object... params) throws SQLException;

    int executeUpdate(String sql, PreparedStatementSetter preparedStatementSetter, Object... params) throws SQLException;

    /**
     * 调用 存储过程， callable PreparedStatement
     */
    <T> T call(String sql, List<?> params, ResultSetExtractor<T> extractor) throws SQLException;
}
