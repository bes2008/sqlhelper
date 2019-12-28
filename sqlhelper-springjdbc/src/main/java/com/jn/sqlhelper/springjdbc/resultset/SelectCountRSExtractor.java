package com.jn.sqlhelper.springjdbc.resultset;

import com.jn.sqlhelper.common.resultset.SelectCountResultSetExtractor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SelectCountRSExtractor implements ResultSetExtractor<Integer> {
    private SelectCountResultSetExtractor resultSetExtractor;
    public SelectCountRSExtractor (SelectCountResultSetExtractor resultSetExtractor){
        this.resultSetExtractor = resultSetExtractor;
    }
    public SelectCountRSExtractor(){
        this(SelectCountResultSetExtractor.INSTANCE);
    }

    @Override
    public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
        return resultSetExtractor.extract(rs);
    }
}
