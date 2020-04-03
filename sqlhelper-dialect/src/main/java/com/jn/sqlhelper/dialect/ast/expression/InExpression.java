package com.jn.sqlhelper.dialect.ast.expression;

import com.jn.langx.expression.operator.AbstractBinaryOperator;

public class InExpression extends AbstractBinaryOperator<SQLExpression, ListExpression, SQLExpression> implements SQLExpression<SQLExpression>, Notable {

    private boolean isNotExpression;

    public InExpression() {

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
        builder.append(getLeft().toString());
        if (isNotExpression) {
            builder.append(" not");
        }
        builder.append(" in (").append(getRight().toString()).append(")");
        return builder.toString();
    }
}
