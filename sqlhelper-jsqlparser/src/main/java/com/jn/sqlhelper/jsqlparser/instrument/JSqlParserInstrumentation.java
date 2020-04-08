package com.jn.sqlhelper.jsqlparser.instrument;

import com.jn.langx.lifecycle.InitializationException;
import com.jn.sqlhelper.dialect.instrument.Instrumentation;
import com.jn.sqlhelper.dialect.instrument.orderby.OrderByTransformer;
import com.jn.sqlhelper.dialect.instrument.where.WhereTransformer;
import com.jn.sqlhelper.dialect.sqlparser.SqlParser;
import com.jn.sqlhelper.jsqlparser.sqlparser.JSqlParserStatementWrapper;
import net.sf.jsqlparser.statement.Statement;

public class JSqlParserInstrumentation implements Instrumentation<Statement, JSqlParserStatementWrapper> {
    private boolean enabled = false;
    private SqlParser<JSqlParserStatementWrapper> sqlParser;
    private WhereTransformer<Statement> whereTransformer;
    private OrderByTransformer<Statement> orderByTransformer;

    @Override
    public void init() throws InitializationException {

    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public SqlParser<JSqlParserStatementWrapper> getSqlParser() {
        return this.sqlParser;
    }

    @Override
    public WhereTransformer<Statement> getWhereTransformer() {
        return this.whereTransformer;
    }

    @Override
    public OrderByTransformer<Statement> getOrderByTransformer() {
        return this.orderByTransformer;
    }

}
