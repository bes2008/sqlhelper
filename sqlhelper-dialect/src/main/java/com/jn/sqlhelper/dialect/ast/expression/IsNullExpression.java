package com.jn.sqlhelper.dialect.ast.expression;

import com.jn.langx.expression.operator.AbstractBinaryOperator;

public class IsNullExpression extends AbstractBinaryOperator<SQLExpression, SQLExpression, SQLExpression> implements SQLExpression<SQLExpression>, Notable {
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
        builder.append(getLeft().toString())
                .append(" is")
                .append(not() ? " not" : "")
                .append(" null");
        return builder.toString();
    }
}
