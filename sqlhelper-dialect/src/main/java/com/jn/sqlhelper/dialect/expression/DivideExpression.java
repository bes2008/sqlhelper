package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.expression.operator.AbstractBinaryOperator;

public class DivideExpression extends AbstractBinaryOperator<SQLExpression, SQLExpression, SQLExpression> implements SQLExpression<SQLExpression>, SymbolExpression {
    public DivideExpression() {
        setOperateSymbol("/");
    }

    @Override
    public SQLExpression execute() {
        return null;
    }
}
