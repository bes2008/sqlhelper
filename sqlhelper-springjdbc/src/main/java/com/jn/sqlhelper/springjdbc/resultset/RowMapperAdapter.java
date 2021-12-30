package com.jn.sqlhelper.springjdbc.resultset;

import com.jn.sqlhelper.common.resultset.ResultSetDescription;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RowMapperAdapter<T> implements RowMapper<T> {
    private com.jn.sqlhelper.common.resultset.RowMapper<T> delegate;

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        return delegate.mapping(rs, rowNum, new ResultSetDescription(rs.getMetaData()));
    }

    public RowMapperAdapter (com.jn.sqlhelper.common.resultset.RowMapper<T> rowMapper){
        setDelegate(rowMapper);
    }

    public com.jn.sqlhelper.common.resultset.RowMapper<T> getDelegate() {
        return delegate;
    }

    public void setDelegate(com.jn.sqlhelper.common.resultset.RowMapper<T> delegate) {
        this.delegate = delegate;
    }
}
