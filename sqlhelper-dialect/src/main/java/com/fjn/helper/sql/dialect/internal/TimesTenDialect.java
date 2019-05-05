package com.fjn.helper.sql.dialect.internal;

import com.fjn.helper.sql.dialect.internal.limit.FirstLimitHandler;


public class TimesTenDialect extends AbstractDialect {
    public TimesTenDialect() {
        super();
        setLimitHandler(new FirstLimitHandler() {
            @Override
            public String getLimitString(String querySelect, int offset, int limit) {
                if (offset > 0) {
                    throw new UnsupportedOperationException("query result offset is not supported");
                }
                return new StringBuilder(querySelect.length() + 8).append(querySelect).insert(6, " first " + limit).toString();
            }
        });
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return false;
    }

    @Override
    public boolean isSupportsVariableLimit() {
        return false;
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isUseMaxForLimit() {
        return true;
    }
}
