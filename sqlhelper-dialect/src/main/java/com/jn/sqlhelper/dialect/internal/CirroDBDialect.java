package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.RowSelection;
import com.jn.sqlhelper.dialect.internal.limit.AbstractLimitHandler;

/**
 * select *
 * from table
 * where xxx
 * order by xxx
 * limit (start, end)
 *
 * start based on 1
 */
public class CirroDBDialect extends AbstractDialect {
    public CirroDBDialect() {
        super();
        setLimitHandler(new AbstractLimitHandler() {
            @Override
            public String processSql(String sql, RowSelection rowSelection) {
                return sql + " limit (?, ?) ";
            }

            @Override
            protected long convertToFirstRowValue(long zeroBasedFirstResult) {
                return zeroBasedFirstResult + 1;
            }
        });
    }

    @Override
    public boolean isUseMaxForLimit() {
        return true;
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
    public boolean isSupportsVariableLimit() {
        return true;
    }

    @Override
    public boolean isBindLimitParametersInReverseOrder() {
        return false;
    }
}
