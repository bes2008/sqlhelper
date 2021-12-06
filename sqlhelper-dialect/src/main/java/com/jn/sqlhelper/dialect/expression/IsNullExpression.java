package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.el.expression.operator.AbstractUnaryOperator;
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

    public int hashCode() {
        return new HashCodeBuilder().with(isNotExpression).with(super.hashCode()).build();
    }
}
