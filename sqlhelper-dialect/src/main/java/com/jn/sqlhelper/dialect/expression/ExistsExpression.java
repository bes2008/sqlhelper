package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.expression.operator.AbstractUnaryOperator;
import com.jn.langx.util.hash.HashCodeBuilder;

public class ExistsExpression extends AbstractUnaryOperator<SQLExpression<SQLExpression>, SQLExpression> implements SQLExpression<SQLExpression>, Notable, SymbolExpression {
    private boolean isNotExpression;

    public ExistsExpression() {
        this(false);
    }

    public ExistsExpression(boolean isNotExpression) {
        setOperateSymbol("exists");
        not(isNotExpression);
    }

    @Override
    public void not(boolean isNotExpression) {
        this.isNotExpression = isNotExpression;
    }

    @Override
    public boolean not() {
        return this.isNotExpression;
    }

    @Override
    public SQLExpression execute() {
        return getTarget();
    }

    @Override
    public String toString() {
        return new StringBuilder(255)
                .append(not() ? "not" : "")
                .append(" " + getOperateSymbol())
                .append(" (")
                .append(getTarget().toString())
                .append(")")
                .toString();

    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().with(super.hashCode()).with(not()).build();
    }
}
