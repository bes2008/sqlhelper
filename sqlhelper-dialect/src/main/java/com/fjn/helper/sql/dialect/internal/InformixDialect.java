package com.fjn.helper.sql.dialect.internal;

import com.fjn.helper.sql.dialect.internal.limit.FirstLimitHandler;
import com.fjn.helper.sql.dialect.internal.urlparser.InformixUrlParser;

import java.util.Locale;


public class InformixDialect extends AbstractDialect {
    public InformixDialect() {
        super();
        setUrlParser(new InformixUrlParser());
        setLimitHandler(new FirstLimitHandler() {
            @Override
            public String getLimitString(String querySelect, int offset, int limit) {
                if (offset > 0) {
                    throw new UnsupportedOperationException("query result offset is not supported");
                }
                return new StringBuilder(querySelect.length() + 8).append(querySelect).insert(querySelect.toLowerCase(Locale.ROOT).indexOf("select") + 6, " first " + limit).toString();
            }
        });
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isUseMaxForLimit() {
        return true;
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return false;
    }

    @Override
    public boolean isSupportsVariableLimit() {
        return false;
    }
}
