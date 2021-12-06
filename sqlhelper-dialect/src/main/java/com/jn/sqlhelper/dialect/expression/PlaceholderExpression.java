package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.el.expression.BaseExpression;
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
    public int hashCode() {
        return new HashCodeBuilder().with(placeholder).build();
    }
}
