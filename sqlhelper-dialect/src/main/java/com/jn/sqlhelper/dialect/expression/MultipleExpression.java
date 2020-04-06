package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.expression.operator.AbstractBinaryOperator;

public class MultipleExpression extends AbstractBinaryOperator<SQLExpression, SQLExpression, SQLExpression> implements SQLExpression<SQLExpression> {
    public MultipleExpression() {
        setOperateSymbol("*");
    }

    @Override
    public SQLExpression execute() {
        return this;
    }
}
