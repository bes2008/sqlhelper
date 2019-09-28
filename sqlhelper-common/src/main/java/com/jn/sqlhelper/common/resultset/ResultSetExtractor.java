package com.jn.sqlhelper.common.resultset;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetExtractor<T> {
    @Nullable
    T extract(@NonNull ResultSet rs) throws SQLException;
}
