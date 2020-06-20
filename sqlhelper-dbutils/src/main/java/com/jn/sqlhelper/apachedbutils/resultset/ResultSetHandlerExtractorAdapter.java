package com.jn.sqlhelper.apachedbutils.resultset;

import com.jn.sqlhelper.common.resultset.ResultSetExtractor;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetHandlerExtractorAdapter<T,E extends ResultSetExtractor<T>> implements ResultSetHandler<T> {
    private E resultSetExtractor;

    public ResultSetHandlerExtractorAdapter(){

    }

    public ResultSetHandlerExtractorAdapter(E resultSetExtractor){
        setResultSetExtractor(resultSetExtractor);
    }

    public void setResultSetExtractor(E resultSetExtractor) {
        this.resultSetExtractor = resultSetExtractor;
    }

    @Override
    public T handle(ResultSet rs) throws SQLException {
        return resultSetExtractor.extract(rs);
    }
}
