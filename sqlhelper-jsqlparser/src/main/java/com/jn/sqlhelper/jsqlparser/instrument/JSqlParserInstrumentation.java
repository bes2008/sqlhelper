package com.jn.sqlhelper.jsqlparser.instrument;

import com.jn.langx.annotation.Name;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.sqlhelper.dialect.instrument.Instrumentation;
import com.jn.sqlhelper.dialect.instrument.orderby.OrderByTransformer;
import com.jn.sqlhelper.dialect.instrument.where.WhereTransformer;
import com.jn.sqlhelper.dialect.sqlparser.SqlParser;
import com.jn.sqlhelper.jsqlparser.sqlparser.JSqlParser;
import com.jn.sqlhelper.jsqlparser.sqlparser.JSqlParserStatementWrapper;
import net.sf.jsqlparser.statement.Statement;

@Name("jsqlparser")
public class JSqlParserInstrumentation implements Instrumentation<Statement, JSqlParserStatementWrapper> {
    private boolean enabled = false;
    private boolean inited = false;
    private SqlParser<JSqlParserStatementWrapper> sqlParser;
    private WhereTransformer<Statement> whereTransformer;
    private OrderByTransformer<Statement> orderByTransformer;

    @Override
    public void init() throws InitializationException {
        if (!inited) {
            inited = true;
            this.sqlParser = new JSqlParser();

            whereTransformer = new JSqlParserWhereTransformer();
            whereTransformer.init();
            orderByTransformer = new JSqlParserOrderByTransformer();
            orderByTransformer.init();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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
