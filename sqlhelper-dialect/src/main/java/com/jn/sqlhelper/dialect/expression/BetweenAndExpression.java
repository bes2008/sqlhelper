package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.util.Preconditions;

public class BetweenAndExpression implements SQLExpression<SQLExpression>, Notable {
    private SQLExpression target;
    private SQLExpression low;
    private SQLExpression high;
    private boolean isNotExpression = false;

    public BetweenAndExpression() {
    }

    public BetweenAndExpression(boolean isNotExpression) {
        this.isNotExpression = isNotExpression;
    }

    @Override
    public void not(boolean isNotExpression) {
        this.isNotExpression = isNotExpression;
    }

    @Override
    public boolean not() {
        return isNotExpression;
    }

    public void setTarget(SQLExpression target) {
        this.target = target;
    }

    public void setLow(SQLExpression low) {
        this.low = low;
    }

    public void setHigh(SQLExpression high) {
        this.high = high;
    }

    @Override
    public SQLExpression execute() {
        return this;
    }

    @Override
    public String toString() {
        Preconditions.checkNotNull(target);
        Preconditions.checkNotNull(low);
        Preconditions.checkNotNull(high);

        StringBuilder builder = new StringBuilder(255);
        builder.append(target.toString())
                .append(not() ? " not" : "")
                .append(" between ")
                .append(low.toString())
                .append(" and ")
                .append(high.toString());
        return builder.toString();
    }
}
