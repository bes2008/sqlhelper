package com.jn.sqlhelper.dialect.ast.expression;

public class StringExpression extends com.jn.langx.expression.value.StringExpression implements SQLExpression<String> {
    public StringExpression(String value) {
        super(value);
    }

    @Override
    public String toString() {
        return "'" + this.execute() + "'";
    }

}
