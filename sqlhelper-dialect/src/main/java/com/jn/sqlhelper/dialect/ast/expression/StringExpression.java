package com.jn.sqlhelper.dialect.ast.expression;

public class StringExpression extends com.jn.langx.expression.value.StringExpression implements SQLExpression<String> {
    public StringExpression(String value) {
        super(value);
    }

    @Override
    public String toString() {
        return "'" + this.execute() + "'";
    }

    @Override
    public boolean equals(Object obj) {
        if(this==obj){
            return true;
        }
        if(obj instanceof StringExpression){
            return toString().equals(obj.toString());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
