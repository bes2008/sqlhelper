package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.el.expression.operator.AbstractUnaryOperator;
import com.jn.langx.util.Objs;
import com.jn.langx.util.hash.HashCodeBuilder;

public class IsNullExpression extends AbstractUnaryOperator<SQLExpression<SQLExpression>, SQLExpression> implements SQLExpression<SQLExpression>, Notable, SymbolExpression {
    private boolean isNotExpression;

    public IsNullExpression() {
        this(false);
    }

    public IsNullExpression(boolean isNotExpression) {
        not(isNotExpression);
    }


    @Override
    public SQLExpression execute() {
        return this;
    }

    @Override
    public void not(boolean isNotExpression) {
        this.isNotExpression = isNotExpression;
    }

    @Override
    public boolean not() {
        return isNotExpression;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(255);
        builder.append(getTarget().toString())
                .append(" is")
                .append(not() ? " not" : "")
                .append(" null");
        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj==null){
            return false;
        }
        if(obj.getClass()!= IsNullExpression.class){
            return false;
        }
        IsNullExpression that = (IsNullExpression)obj;
        if(!Objs.equals(this.isNotExpression, that.isNotExpression)){
            return false;
        }
        if(!Objs.equals(this.toString(), that.toString())){
            return false;
        }
        return true;

    }

    public int hashCode() {
        return new HashCodeBuilder().with(isNotExpression).with(super.hashCode()).build();
    }
}
