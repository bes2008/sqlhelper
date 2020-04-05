package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.expression.operator.AbstractBinaryOperator;
import com.jn.langx.util.Strings;

/**
 * left [not] like right escape '/'
 */
public class LikeExpression extends AbstractBinaryOperator<SQLExpression, StringExpression, SQLExpression> implements SQLExpression<SQLExpression>, Notable {
    private boolean isNotExpression = false;
    private String escape; // optional

    public LikeExpression() {
    }

    public LikeExpression(boolean isNotExpression) {
        not(isNotExpression);
    }

    public void setEscape(String escape) {
        this.escape = escape;
    }

    @Override
    public SQLExpression execute() {
        return null;
    }

    @Override
    public void not(boolean isNotExpression) {
        this.isNotExpression = isNotExpression;
    }

    @Override
    public boolean not() {
        return isNotExpression;
    }

    public SQLExpression getPatternExpression() {
        return getRight();
    }

    public String getPatternString() {
        return getPatternExpression().toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(255);
        builder.append(getLeft().toString())
                .append(not() ? " not" : "")
                .append(" like ")
                .append(getPatternExpression().toString())
                .append(Strings.isEmpty(escape) ? "" : (" " + escape));
        return builder.toString();
    }
}
