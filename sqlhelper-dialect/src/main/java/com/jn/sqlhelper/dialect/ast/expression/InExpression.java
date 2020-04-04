package com.jn.sqlhelper.dialect.ast.expression;

import com.jn.langx.expression.operator.AbstractBinaryOperator;

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
                .append(not() ? " not" : "")
                .append(" in (")
                .append(getRight().toString())
                .append(")");
        return builder.toString();
    }
}
