package com.jn.sqlhelper.dialect.sqlparser;

public interface SqlStatementWrapper<Statement>  {
    String getOriginalSql();
    void setOriginalSql(String sql);
    Statement get();
}
