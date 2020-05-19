package com.jn.sqlhelper.dialect.expression;

public class SQLExpressions {
    private SQLExpressions() {
    }

    public static boolean isPlaceholderExpression(SQLExpression expression) {
        if (expression == null) {
            return false;
        }
        if (expression instanceof PlaceholderExpression) {
            return true;
        }
        return false;
    }
}
