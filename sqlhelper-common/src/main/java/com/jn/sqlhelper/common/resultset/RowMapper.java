package com.jn.sqlhelper.common.resultset;

import java.sql.ResultSet;

public interface RowMapper<T> {
    T mapping(ResultSet row, int currentRowNo);
}
