package com.jn.sqlhelper.dialect.expression;

public class ValueExpression<E> extends com.jn.langx.expression.value.ValueExpression<E> implements SQLExpression<E>{
    public ValueExpression() {
    }

    public ValueExpression(E value) {
        super(value);
    }

}
