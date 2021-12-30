package com.jn.sqlhelper.apachedbutils.template;

import com.jn.sqlhelper.apachedbutils.QueryRunner;
import com.jn.sqlhelper.apachedbutils.resultset.ResultSetHandlerExtractorAdapter;
import com.jn.sqlhelper.apachedbutils.resultset.RowMapperResultSetHandler;
import com.jn.sqlhelper.apachedbutils.resultset.SingleRecordRowMapperResultSetHandler;
import com.jn.sqlhelper.apachedbutils.statement.ArrayPreparedStatementSetter;
import com.jn.sqlhelper.apachedbutils.statement.PreparedStatementSetterAdapter;
import com.jn.sqlhelper.common.jdbc.JdbcTemplate;
import com.jn.sqlhelper.common.resultset.ResultSetExtractor;
import com.jn.sqlhelper.common.resultset.RowMapper;
import com.jn.sqlhelper.common.statement.PreparedStatementSetter;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

public class DbutilsJdbcTemplate implements JdbcTemplate {
    private QueryRunner queryRunner;

    public DbutilsJdbcTemplate(QueryRunner queryRunner){
        this.queryRunner=queryRunner;
    }

    @Override
    public DataSource getDataSource() {
        return queryRunner.getDataSource();
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        return queryRunner.execute(sql);
    }

    @Override
    public <T> T executeQuery(String sql, PreparedStatementSetter preparedStatementSetter, ResultSetExtractor<T> extractor, Object... params) throws SQLException {
        return queryRunner.query(sql, new PreparedStatementSetterAdapter(preparedStatementSetter, params), new ResultSetHandlerExtractorAdapter<T, ResultSetExtractor<T>>(extractor));
    }

    @Override
    public <T> T executeQuery(String sql, ResultSetExtractor<T> extractor, Object... params) throws SQLException {
        com.jn.sqlhelper.apachedbutils.statement.PreparedStatementSetter preparedStatementSetter = new ArrayPreparedStatementSetter(params);
        return queryRunner.query(sql, preparedStatementSetter, new ResultSetHandlerExtractorAdapter<T, ResultSetExtractor<T>>(extractor));
    }

    @Override
    public <T> List<T> queryList(String sql, RowMapper<T> rowMapper, Object... params) throws SQLException {
        com.jn.sqlhelper.apachedbutils.statement.PreparedStatementSetter preparedStatementSetter = new ArrayPreparedStatementSetter(params);
        return queryRunner.query(sql, preparedStatementSetter, new RowMapperResultSetHandler<T>(rowMapper));
    }

    @Override
    public <T> T queryOne(String sql, RowMapper<T> rowMapper, Object... params) throws SQLException {
        com.jn.sqlhelper.apachedbutils.statement.PreparedStatementSetter preparedStatementSetter = new ArrayPreparedStatementSetter(params);
        return queryRunner.query(sql, preparedStatementSetter, new SingleRecordRowMapperResultSetHandler<T>(rowMapper));
    }

    @Override
    public int executeUpdate(String sql, PreparedStatementSetter preparedStatementSetter, Object... params) throws SQLException {
        return queryRunner.executeUpdate(sql, new PreparedStatementSetterAdapter(preparedStatementSetter, params), params);
    }

    @Override
    public <T> T call(String sql, List<?> params, ResultSetExtractor<T> extractor) throws SQLException {
        List<?> ret = queryRunner.execute(sql, new ResultSetHandlerExtractorAdapter(extractor), params.toArray());
        return (T) ret;
    }
}
