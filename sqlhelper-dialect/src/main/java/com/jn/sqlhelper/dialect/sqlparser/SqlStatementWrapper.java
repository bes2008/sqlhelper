package com.jn.sqlhelper.dialect.sqlparser;

public interface SqlStatementWrapper<Statement>  {
    String getOriginalSql();
    void setOriginalSql(String sql);

    void setStatement(Statement statement);
    Statement get();

    /**
     * sql has changed ?
     * @return true if the original sql was changed
     */
    boolean isChanged();

    void setChanged(boolean changed);

    /**
     *
     * @return the new sql
     */
    String getSql();
}
