package com.jn.sqlhelper.dialect.sqlparser;

import net.sf.jsqlparser.statement.Statement;

/**
 * @author huxiongming
 */
public class StatementWrapper extends AbstractSqlStatementWrapper<Statement> {
    @Override
    public String getSql() {
        if (!isChanged()) {
            return getOriginalSql();
        }
        return get().toString();
    }
}
