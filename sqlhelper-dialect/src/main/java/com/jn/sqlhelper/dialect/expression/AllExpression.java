package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.el.expression.operator.AbstractUnaryOperator;

public class AllExpression extends AbstractUnaryOperator<SQLExpression<SQLExpression>, SQLExpression> implements SQLExpression<SQLExpression>,SymbolExpression {

    public AllExpression() {
        setOperateSymbol("all");
    }

    @Override
    public SQLExpression execute() {
        return getTarget();
    }

    @Override
    public String toString() {
        return new StringBuilder(255)
                .append(getOperateSymbol())
                .append(" (")
                .append(getTarget().toString())
                .append(")")
                .toString();

    }
}
