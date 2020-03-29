package com.jn.sqlhelper.dialect.sqlparser.jsqlparser;

import com.jn.langx.util.Preconditions;
import com.jn.sqlhelper.dialect.sqlparser.SqlStatementWrapper;
import net.sf.jsqlparser.statement.Statement;

public class JSqlParserStatementWrapper implements SqlStatementWrapper<Statement> {
    private Statement statement;
    private String sql;
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
}
