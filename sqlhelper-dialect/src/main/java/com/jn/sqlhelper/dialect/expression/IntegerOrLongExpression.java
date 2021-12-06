package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.el.expression.value.NumberExpression;

public class IntegerOrLongExpression extends NumberExpression<Long> implements SQLExpression<Long> {
    public IntegerOrLongExpression() {
        super();
    }

    public IntegerOrLongExpression(long number) {
        super(number);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
