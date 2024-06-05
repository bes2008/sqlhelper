package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.el.expression.BaseExpression;
import com.jn.langx.util.Objs;
import com.jn.langx.util.hash.HashCodeBuilder;

public final class PlaceholderExpression extends BaseExpression<SQLExpression> implements SQLExpression<SQLExpression> {
    public final String placeholder = "?";

    public PlaceholderExpression() {
    }

    @Override
    public SQLExpression execute() {
        return this;
    }

    @Override
    public String toString() {
        return placeholder;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj==null){
            return false;
        }
        if(obj.getClass()!= PlaceholderExpression.class){
            return false;
        }
        PlaceholderExpression that = (PlaceholderExpression)obj;
        if(!Objs.equals(this.toString(), that.toString())){
            return false;
        }
        return true;

    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().with(placeholder).build();
    }
}
