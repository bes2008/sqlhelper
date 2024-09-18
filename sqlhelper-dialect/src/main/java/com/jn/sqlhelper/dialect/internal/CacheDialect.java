package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.LimitHelper;
import com.jn.sqlhelper.dialect.internal.limit.TopLimitHandler;
import com.jn.sqlhelper.dialect.pagination.RowSelection;

import java.sql.CallableStatement;
import java.sql.SQLException;


public class CacheDialect extends AbstractDialect {
    public CacheDialect() {
        super();
        setLimitHandler(new TopLimitHandler() {
            public String processSql(String sql,boolean isSubquery, boolean useLimitVariable, RowSelection rowSelection) {
                if (rowSelection.hasOffset()) {
                    throw new UnsupportedOperationException("query result offset is not supported");
                }

                int insertionPoint = sql.startsWith("select distinct") ? 15 : 6;
                String substring="";
                if(useLimitVariable && isUseLimitInVariableMode(isSubquery)){
                    substring = " TOP ? ";
                }else {
                    int lastRow = getMaxOrLimit(rowSelection);
                    substring = " TOP " + lastRow ;
                }
                return new StringBuilder(sql.length() + 8).append(sql).insert(insertionPoint, substring).toString();
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
