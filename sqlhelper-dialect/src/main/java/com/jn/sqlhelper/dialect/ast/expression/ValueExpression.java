package com.jn.sqlhelper.dialect.ast.expression;

public class ValueExpression extends com.jn.langx.expression.value.ValueExpression implements SQLExpression{
    public ValueExpression() {
    }

    public ValueExpression(Object value) {
        super(value);
    }

}
