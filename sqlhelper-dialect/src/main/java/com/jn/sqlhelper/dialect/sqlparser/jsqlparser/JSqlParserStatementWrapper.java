package com.jn.sqlhelper.dialect.sqlparser.jsqlparser;

import com.jn.langx.util.Preconditions;
import com.jn.sqlhelper.dialect.sqlparser.SqlStatementWrapper;
import net.sf.jsqlparser.statement.Statement;

public class JSqlParserStatementWrapper implements SqlStatementWrapper<Statement> {
    private Statement statement;
    private String sql;
    private boolean changed = false;

    public JSqlParserStatementWrapper(Statement statement){
        Preconditions.checkNotNull(statement);
        this.statement = statement;
    }

    @Override
    public String getOriginalSql() {
        return sql;
    }

    @Override
    public void setOriginalSql(String sql) {
        this.sql=sql;
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

    @Override
    public String getSql() {
        if(!isChanged()){
            return getOriginalSql();
        }
        return get().toString();
    }
}
