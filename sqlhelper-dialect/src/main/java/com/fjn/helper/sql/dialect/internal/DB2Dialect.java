package com.fjn.helper.sql.dialect.internal;

import com.fjn.helper.sql.dialect.internal.limit.LimitHelper;
import com.fjn.helper.sql.dialect.RowSelection;
import com.fjn.helper.sql.dialect.internal.limit.AbstractLimitHandler;

import java.sql.CallableStatement;
import java.sql.SQLException;


public class DB2Dialect extends AbstractDialect {
    public DB2Dialect() {
        super();
        setLimitHandler(new AbstractLimitHandler() {
            @Override
            public String processSql(String sql, RowSelection selection) {
                if (DB2Dialect.this.isDB2_400OrNewer()) {
                    if (LimitHelper.hasFirstRow(selection)) {
                        return "select * from ( select inner2_.*, rownumber() over(order by order of inner2_) as rownumber_ from ( " + sql + " fetch first " + getMaxOrLimit(selection) + " rows only ) as inner2_ ) as inner1_ where rownumber_ > " + selection.getOffset() + " order by rownumber_";
                    }
                    return sql + " fetch first " + getMaxOrLimit(selection) + " rows only";
                }
                int limit = selection.getLimit().intValue();
                if (limit == 0) {
                    return sql;
                }
                return sql + " fetch first " + limit + " rows only ";
            }
        });
    }


    private boolean isDB2_400OrNewer() {
        return true;
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isSupportsVariableLimit() {
        return false;
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
