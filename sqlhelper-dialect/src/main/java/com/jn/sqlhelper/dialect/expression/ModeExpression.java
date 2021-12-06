package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.el.expression.operator.AbstractBinaryOperator;

public class ModeExpression extends AbstractBinaryOperator<SQLExpression, SQLExpression, SQLExpression> implements SQLExpression<SQLExpression>,SymbolExpression {
    public ModeExpression() {
        setOperateSymbol("%");
    }

    @Override
    public SQLExpression execute() {
        return this;
    }
}
