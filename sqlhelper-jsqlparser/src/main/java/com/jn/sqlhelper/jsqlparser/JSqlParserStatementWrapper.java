package com.jn.sqlhelper.jsqlparser;

import com.jn.langx.util.Preconditions;
import com.jn.sqlhelper.dialect.sqlparser.AbstractSqlStatementWrapper;
import net.sf.jsqlparser.statement.Statement;

public class JSqlParserStatementWrapper extends AbstractSqlStatementWrapper<Statement> {

    public JSqlParserStatementWrapper(Statement statement) {
        Preconditions.checkNotNull(statement);
        setStatement(statement);
    }

    @Override
    public String getSql() {
        if (!isChanged()) {
            return getOriginalSql();
        }
        return get().toString();
    }
}
