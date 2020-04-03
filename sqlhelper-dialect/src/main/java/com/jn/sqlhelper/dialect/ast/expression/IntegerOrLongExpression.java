package com.jn.sqlhelper.dialect.ast.expression;

import com.jn.langx.expression.value.NumberExpression;

public class IntegerOrLongExpression extends NumberExpression<Long> implements SQLExpression<Long>{
    public IntegerOrLongExpression(long number) {
        super(number);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
