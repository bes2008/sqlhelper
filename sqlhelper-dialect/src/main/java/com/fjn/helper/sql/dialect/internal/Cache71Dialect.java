package com.fjn.helper.sql.dialect.internal;

import com.fjn.helper.sql.dialect.internal.limit.TopLimitHandler;

import java.sql.CallableStatement;
import java.sql.SQLException;


public class Cache71Dialect extends AbstractDialect {
    public Cache71Dialect() {
        super();
        setLimitHandler(new TopLimitHandler() {
            @Override
            public String getLimitString(String sql, boolean hasOffset) {
                if (hasOffset) {
                    throw new UnsupportedOperationException("query result offset is not supported");
                }


                int insertionPoint = sql.startsWith("select distinct") ? 15 : 6;


                return new StringBuilder(sql.length() + 8).append(sql).insert(insertionPoint, " TOP ? ").toString();
            }
        });
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return false;
    }
    @Override
    public boolean isSupportsVariableLimit() {
        return true;
    }

    @Override
    public boolean isBindLimitParametersFirst() {
        return true;
    }

    @Override
    public boolean isUseMaxForLimit() {
        return true;
    }

    @Override
    public int registerResultSetOutParameter(CallableStatement statement, int col)
            throws SQLException {
        return col;
    }
}
