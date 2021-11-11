package com.jn.sqlhelper.apachedbutils;

import com.jn.sqlhelper.common.jdbc.JdbcTemplate;
import com.jn.sqlhelper.common.resultset.ResultSetExtractor;
import com.jn.sqlhelper.common.resultset.RowMapper;
import com.jn.sqlhelper.common.statement.PreparedStatementSetter;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

public class DbutilsJdbcTemplateAdapter implements JdbcTemplate {
    private QueryRunner queryRunner;

    @Override
    public DataSource getDataSource() {
        return null;
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        return false;
    }

    @Override
    public <T> T executeQuery(String sql, PreparedStatementSetter preparedStatementSetter, ResultSetExtractor<T> extractor, Object... params) throws SQLException {
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
