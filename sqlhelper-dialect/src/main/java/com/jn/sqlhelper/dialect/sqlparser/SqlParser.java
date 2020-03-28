package com.jn.sqlhelper.dialect.sqlparser;

public interface SqlParser<SQL extends SqlStatementWrapper> {
    SQL parse(String sql) throws SQLParseException;
}
