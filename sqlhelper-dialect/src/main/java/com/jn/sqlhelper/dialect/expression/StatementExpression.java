package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.el.expression.BaseExpression;
import com.jn.langx.util.hash.HashCodeBuilder;

public class StatementExpression extends BaseExpression<SQLExpression> implements SQLExpression<SQLExpression> {
    private String statement;

    public StatementExpression() {
    }

    public StatementExpression(String statement) {
        setStatement(statement);
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getStatement(){
        return this.statement;
    }

    @Override
    public StatementExpression execute() {
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().with(statement).build();
    }

    @Override
    public String toString() {
        return statement;
    }
}
