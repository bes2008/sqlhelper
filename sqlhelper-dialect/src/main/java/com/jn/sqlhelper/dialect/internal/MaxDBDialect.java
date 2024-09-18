package com.jn.sqlhelper.dialect.internal;

import com.jn.langx.annotation.Name;
import com.jn.sqlhelper.dialect.pagination.RowSelection;
import com.jn.sqlhelper.dialect.internal.limit.AbstractLimitHandler;
import com.jn.sqlhelper.dialect.internal.limit.LimitHelper;

@Name("maxdb")
public class MaxDBDialect extends AbstractDialect {

    public MaxDBDialect() {
        super();
        setLimitHandler(new AbstractLimitHandler() {
            @Override
            public String processSql(String sql, boolean isSubquery, boolean useLimitVariable, RowSelection selection) {
                boolean hasOffset = LimitHelper.hasFirstRow(selection);
                if(useLimitVariable && isUseLimitInVariableMode(isSubquery)) {
                    sql = "select * from (" + sql + ") where rowno < ? ";
                    if (hasOffset) {
                        sql = sql + " and rowno >= ?";
                    }
                }else{
                    int firstRow = (int)convertToFirstRowValue(LimitHelper.getFirstRow(selection));
                    int lastRow = getMaxOrLimit(selection);
                    sql = "select * from (" + sql + ") where rowno < " + lastRow;
                    if (hasOffset) {
                        sql = sql + " and rowno >= " + firstRow;
                    }
                }
                return sql;
            }
        });
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return true;
    }

    @Override
    public boolean isBindLimitParametersInReverseOrder() {
        return false;
    }
}
