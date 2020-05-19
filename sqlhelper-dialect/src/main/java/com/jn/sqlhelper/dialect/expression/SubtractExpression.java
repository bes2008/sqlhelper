package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.expression.operator.AbstractBinaryOperator;

public class SubtractExpression extends AbstractBinaryOperator<SQLExpression, SQLExpression, SQLExpression> implements SQLExpression<SQLExpression>, SymbolExpression {
    public SubtractExpression() {
        setOperateSymbol("-");
    }

    @Override
    public SQLExpression execute() {
        return this;
    }
}
