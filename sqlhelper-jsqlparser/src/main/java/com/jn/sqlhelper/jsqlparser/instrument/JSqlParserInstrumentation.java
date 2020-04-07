package com.jn.sqlhelper.jsqlparser.instrument;

import com.jn.sqlhelper.dialect.instrument.Instrumentation;
import com.jn.sqlhelper.dialect.instrument.orderby.OrderByTransformer;
import com.jn.sqlhelper.dialect.instrument.where.WhereTransformer;
import com.jn.sqlhelper.dialect.sqlparser.SqlParser;
import com.jn.sqlhelper.jsqlparser.JSqlParserStatementWrapper;
import net.sf.jsqlparser.statement.Statement;

public class JSqlParserInstrumentation implements Instrumentation<Statement, JSqlParserStatementWrapper> {
    @Override
    public SqlParser<JSqlParserStatementWrapper> getSqlParser() {
        return null;
    }

    @Override
    public WhereTransformer<Statement> getWhereTransformer() {
        return null;
    }

    @Override
    public OrderByTransformer<Statement> getOrderByTransformer() {
        return null;
    }
}
