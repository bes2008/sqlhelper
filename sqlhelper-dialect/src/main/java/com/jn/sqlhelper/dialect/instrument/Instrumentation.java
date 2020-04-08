package com.jn.sqlhelper.dialect.instrument;

import com.jn.langx.lifecycle.Initializable;
import com.jn.sqlhelper.dialect.instrument.orderby.OrderByTransformer;
import com.jn.sqlhelper.dialect.instrument.where.WhereTransformer;
import com.jn.sqlhelper.dialect.sqlparser.SqlParser;
import com.jn.sqlhelper.dialect.sqlparser.SqlStatementWrapper;

public interface Instrumentation<Statement, SQL extends SqlStatementWrapper<Statement>> extends Initializable {
    SqlParser<SQL> getSqlParser();

    WhereTransformer<Statement> getWhereTransformer();

    OrderByTransformer<Statement> getOrderByTransformer();

    boolean isEnabled();
}
