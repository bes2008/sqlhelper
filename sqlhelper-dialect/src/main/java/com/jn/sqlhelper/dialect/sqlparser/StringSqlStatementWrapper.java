package com.jn.sqlhelper.dialect.sqlparser;

public class StringSqlStatementWrapper extends AbstractSqlStatementWrapper<String> {
    @Override
    public String getSql() {
        return get();
    }
}
