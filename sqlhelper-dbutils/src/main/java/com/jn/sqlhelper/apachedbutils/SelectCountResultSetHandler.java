package com.jn.sqlhelper.apachedbutils;

import com.jn.sqlhelper.common.resultset.SelectCountResultSetExtractor;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SelectCountResultSetHandler implements ResultSetHandler<Integer> {
    private SelectCountResultSetExtractor resultSetExtractor;

    public SelectCountResultSetHandler() {
        this(SelectCountResultSetExtractor.INSTANCE);
    }

    public SelectCountResultSetHandler(SelectCountResultSetExtractor resultSetExtractor) {
        this.resultSetExtractor = resultSetExtractor;
    }

    @Override
    public Integer handle(ResultSet rs) throws SQLException {
        return resultSetExtractor.extract(rs);
    }
}
