package com.jn.sqlhelper.dialect.instrument;

import com.jn.sqlhelper.dialect.sqlparser.SqlStatementWrapper;

public class InstrumentedStatement implements SqlStatementWrapper {
    private String originalSql;
    @Override
    public String getOriginalSql() {
        return originalSql;
    }

    @Override
    public void setOriginalSql(String sql) {
        this.originalSql = sql;
    }

    @Override
    public Object get() {
        return null;
    }
}
