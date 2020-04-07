package com.jn.sqlhelper.dialect.sqlparser;

public abstract class AbstractSqlStatementWrapper<Statement> implements SqlStatementWrapper<Statement> {
    private String originalSql;
    private boolean changed = false;
    private Statement statement;

    @Override
    public String getOriginalSql() {
        return originalSql;
    }

    @Override
    public void setOriginalSql(String sql) {
        this.originalSql = sql;
    }

    @Override
    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    @Override
    public Statement get() {
        return statement;
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public void setChanged(boolean changed) {
        this.changed = changed;
    }

}
