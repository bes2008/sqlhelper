package com.jn.sqlhelper.dialect.expression;

public class StringExpression extends com.jn.langx.el.expression.value.StringExpression implements SQLExpression<String> {
    public StringExpression() {
        super();
    }

    public StringExpression(String value) {
        super(value);
    }

    @Override
    public String toString() {
        return "'" + this.execute() + "'";
    }

}
