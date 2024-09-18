package com.jn.sqlhelper.dialect.instrument;

import com.jn.langx.lifecycle.InitializationException;
import com.jn.sqlhelper.dialect.sqlparser.SqlStatementWrapper;

public abstract class AbstractClauseTransformer<Statement> implements ClauseTransformer<Statement> {
    private boolean inited = false;
    private Instrumentation<Statement, ? extends SqlStatementWrapper<Statement>> instrumentation;

    @Override
    public Instrumentation<Statement, ? extends SqlStatementWrapper<Statement>> getInstrumentation() {
        return instrumentation;
    }

    @Override
    public void setInstrumentation(Instrumentation<Statement, ? extends SqlStatementWrapper<Statement>> instrumentation) {
        this.instrumentation = instrumentation;
    }

    @Override
    public void init() throws InitializationException {
        if (!inited) {
            inited = true;
            doInit();
        }
    }

    protected void doInit() {
    }

    ;
}
