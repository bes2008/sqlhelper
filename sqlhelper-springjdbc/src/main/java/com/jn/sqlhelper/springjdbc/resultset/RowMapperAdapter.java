package com.jn.sqlhelper.springjdbc.resultset;

import com.jn.sqlhelper.common.resultset.ResultSetDescription;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RowMapperAdapter implements RowMapper {
    private com.jn.sqlhelper.common.resultset.RowMapper delegate;

    @Override
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        return delegate.mapping(rs, rowNum, new ResultSetDescription(rs.getMetaData()));
    }

    public RowMapperAdapter (com.jn.sqlhelper.common.resultset.RowMapper rowMapper){
        setDelegate(rowMapper);
    }

    public com.jn.sqlhelper.common.resultset.RowMapper getDelegate() {
        return delegate;
    }

    public void setDelegate(com.jn.sqlhelper.common.resultset.RowMapper delegate) {
        this.delegate = delegate;
    }
}
