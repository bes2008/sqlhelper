package com.jn.sqlhelper.dialect.sqlparser;

public interface SqlStatementWrapper<Statement>  {
    String getSql();
    void setSql(String sql);
    Statement get();
}
