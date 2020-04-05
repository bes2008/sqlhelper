package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.expression.operator.AbstractBinaryOperator;
import com.jn.langx.util.hash.HashCodeBuilder;

public class InExpression extends AbstractBinaryOperator<SQLExpression, ListExpression, SQLExpression> implements SQLExpression<SQLExpression>, Notable {

    private boolean isNotExpression;

    public InExpression() {
        this(false);
    }

    public InExpression(boolean not) {
        not(not);
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
    public SQLExpression execute() {
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(255);
        builder.append(getLeft().toString())
                .append(not() ? " not in " : " in ")
                .append(getRight().toString());
        return builder.toString();
    }

    public int hashCode(){
        return new HashCodeBuilder().with(not()).with(super.hashCode()).build();
    }
}
