package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.el.expression.value.NumberExpression;

public class DoubleExpression extends NumberExpression<Double> implements SQLExpression<Double> {
    public DoubleExpression() {
        super();
    }

    public DoubleExpression(Double number) {
        super(number);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
