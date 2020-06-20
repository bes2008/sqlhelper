package com.jn.sqlhelper.common.resultset;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Functions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SingleRecordRowMapperResultSetExtractor<T> implements ResultSetExtractor<T>{
    private RowMapper<T> rowMapper;

    public SingleRecordRowMapperResultSetExtractor(RowMapper<T> rowMapper){
        setRowMapper(rowMapper);
    }
    public void setRowMapper(RowMapper<T> rowMapper) {
        this.rowMapper = rowMapper;
    }

    @Override
    public T extract(ResultSet rs) throws SQLException {
        List<T> records = new RowMapperResultSetExtractor<T>(rowMapper).extract(rs);
        return Collects.findFirst(records, Functions.<T>nonNullPredicate());
    }
}
