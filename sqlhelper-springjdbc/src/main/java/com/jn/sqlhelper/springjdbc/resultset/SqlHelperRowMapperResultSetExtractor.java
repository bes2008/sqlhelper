package com.jn.sqlhelper.springjdbc.resultset;

import com.jn.langx.util.Preconditions;
import com.jn.sqlhelper.common.resultset.BeanRowMapper;
import com.jn.sqlhelper.common.resultset.RowMapperResultSetExtractor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SqlHelperRowMapperResultSetExtractor<R> implements ResultSetExtractor<List<R>> {
    private RowMapperResultSetExtractor<R> sqlhelperRSExtractor;

    public SqlHelperRowMapperResultSetExtractor(RowMapperResultSetExtractor<R> delegate) {
        Preconditions.checkNotNull(delegate);
        this.sqlhelperRSExtractor = delegate;
    }

    public SqlHelperRowMapperResultSetExtractor(BeanRowMapper<R> beanRowMapper) {
        Preconditions.checkNotNull(beanRowMapper);
        this.sqlhelperRSExtractor = new RowMapperResultSetExtractor<R>(beanRowMapper);
    }

    @Override
    public List<R> extractData(ResultSet rs) throws SQLException, DataAccessException {
        return this.sqlhelperRSExtractor.extract(rs);
    }
}
