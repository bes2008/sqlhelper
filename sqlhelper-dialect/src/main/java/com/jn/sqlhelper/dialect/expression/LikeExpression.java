package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.el.expression.operator.AbstractBinaryOperator;

/**
 * left [not] like right escape '/'
 */
public class LikeExpression extends AbstractBinaryOperator<SQLExpression, StringExpression, SQLExpression> implements SQLExpression<SQLExpression>, Notable, SymbolExpression {
    private boolean isNotExpression = false;
    private char escape; // BackslashStyleEscaper.INSTANCE.getEscapeChar(); // optional
    private boolean caseInsensitive = false;

    public LikeExpression() {
    }

    public LikeExpression(boolean isNotExpression) {
        not(isNotExpression);
    }

    public void setEscape(char escape) {
        this.escape = escape;
    }

    public char getEscape() {
        return escape;
    }

    public boolean isCaseInsensitive() {
        return caseInsensitive;
    }

    public void setCaseInsensitive(boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
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
                .append(caseInsensitive ? " ilike " : " like ")
                .append(getPatternExpression().toString())
                .append(escape == 0 ? "" : (" escape '" + escape + "'"));
        return builder.toString();
    }
}
