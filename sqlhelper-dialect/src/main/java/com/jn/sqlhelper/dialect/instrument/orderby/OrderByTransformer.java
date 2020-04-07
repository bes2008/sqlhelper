package com.jn.sqlhelper.dialect.instrument.orderby;

import com.jn.sqlhelper.dialect.instrument.ClauseTransformer;
import com.jn.sqlhelper.dialect.sqlparser.SqlStatementWrapper;

public interface OrderByTransformer<Statement> extends ClauseTransformer<Statement> {
    boolean isEnabled();
    boolean isTransformable(SqlStatementWrapper<Statement> statementWrapper);
}
