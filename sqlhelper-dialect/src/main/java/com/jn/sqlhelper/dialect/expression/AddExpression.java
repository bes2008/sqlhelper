package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.expression.operator.AbstractBinaryOperator;

public class AddExpression extends AbstractBinaryOperator<SQLExpression, SQLExpression, SQLExpression> implements SQLExpression<SQLExpression> {
    public AddExpression() {
        setOperateSymbol("+");
    }

    @Override
    public SQLExpression execute() {
        return this;
    }

}
