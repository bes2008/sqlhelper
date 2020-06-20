package com.jn.sqlhelper.springjdbc.resultset;

import com.jn.sqlhelper.common.resultset.ResultSetExtractor;
import org.springframework.dao.DataAccessException;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetExtractorAdaptor<T, E extends ResultSetExtractor<T>> implements org.springframework.jdbc.core.ResultSetExtractor<T> {
    private ResultSetExtractor<T> delegate;

    public ResultSetExtractorAdaptor(E delegate){
        setDelegate(delegate);
    }

    @Override
    public T extractData(ResultSet rs) throws SQLException, DataAccessException {
        return delegate.extract(rs);
    }

    public ResultSetExtractor<T> getDelegate() {
        return delegate;
    }

    public void setDelegate(ResultSetExtractor<T> delegate) {
        this.delegate = delegate;
    }
}
