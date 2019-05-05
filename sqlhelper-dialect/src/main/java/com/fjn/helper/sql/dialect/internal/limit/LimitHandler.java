package com.fjn.helper.sql.dialect.internal.limit;

import com.fjn.helper.sql.dialect.Dialect;
import com.fjn.helper.sql.dialect.RowSelection;

import java.sql.PreparedStatement;
import java.sql.SQLException;


public abstract class LimitHandler {
    protected Dialect dialect;

    public Dialect getDialect() {
        return this.dialect;
    }

    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }


    public abstract String processSql(String sql, RowSelection rowSelection);


    protected String getLimitString(String query, int offset, int limit) {
        return getLimitString(query, (offset > 0) || (getDialect().isForceLimitUsage()));
    }


    protected String getLimitString(String query, boolean hasOffset) {
        throw new UnsupportedOperationException("Paged queries not supported by " + getClass().getName());
    }

    public abstract int bindLimitParametersAtStartOfQuery(RowSelection rowSelection, PreparedStatement preparedStatement, int index)
            throws SQLException;

    public abstract int bindLimitParametersAtEndOfQuery(RowSelection rowSelection, PreparedStatement preparedStatement, int index)
            throws SQLException;

    public abstract void setMaxRows(RowSelection rowSelection, PreparedStatement preparedStatement)
            throws SQLException;
}
