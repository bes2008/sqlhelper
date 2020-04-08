package com.jn.sqlhelper.dialect.instrument;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.lifecycle.Initializable;
import com.jn.sqlhelper.dialect.sqlparser.SqlStatementWrapper;

public interface ClauseTransformer<Statement> extends Initializable {
    Instrumentation<Statement, ? extends SqlStatementWrapper<Statement>> getInstrumentation();
    void setInstrumentation(Instrumentation<Statement, ? extends SqlStatementWrapper<Statement>> instrumentation);
    SqlStatementWrapper<Statement> transform(@NonNull SqlStatementWrapper<Statement> statement, @NonNull TransformConfig config);
}
