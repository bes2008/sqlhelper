package com.jn.sqlhelper.common.resultset;

import com.jn.sqlhelper.common.ddlmodel.ResultSetDescription;

import java.sql.ResultSet;

public interface RowMapper<T> {
    T mapping(ResultSet row, int currentRowNo, ResultSetDescription resultSetDescription);
}
