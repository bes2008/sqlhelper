package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.el.expression.operator.AbstractBinaryOperator;

public class AddExpression extends AbstractBinaryOperator<SQLExpression, SQLExpression, SQLExpression> implements SQLExpression<SQLExpression>,SymbolExpression {
    public AddExpression() {
        setOperateSymbol("+");
    }

    @Override
    public SQLExpression execute() {
        return this;
    }

}
