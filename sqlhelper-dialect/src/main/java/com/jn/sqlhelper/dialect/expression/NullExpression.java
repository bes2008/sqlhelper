package com.jn.sqlhelper.dialect.expression;

public class NullExpression implements SQLExpression<SQLExpression> {
    @Override
    public SQLExpression execute() {
        return this;
    }

    @Override
    public String toString() {
        return "NULL";
    }
}
