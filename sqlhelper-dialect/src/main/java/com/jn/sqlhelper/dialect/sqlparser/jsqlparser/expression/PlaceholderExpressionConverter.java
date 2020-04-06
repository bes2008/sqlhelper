package com.jn.sqlhelper.dialect.sqlparser.jsqlparser.expression;

import com.jn.sqlhelper.dialect.expression.PlaceholderExpression;
import net.sf.jsqlparser.expression.JdbcParameter;

public class PlaceholderExpressionConverter implements ExpressionConverter<PlaceholderExpression, JdbcParameter> {
    @Override
    public JdbcParameter toJSqlParserExpression(PlaceholderExpression expression) {
        return new JdbcParameter();
    }

    @Override
    public PlaceholderExpression fromJSqlParserExpression(JdbcParameter expression) {
        return null;
    }

    @Override
    public Class<PlaceholderExpression> getStandardExpressionClass() {
        return PlaceholderExpression.class;
    }

    @Override
    public Class<JdbcParameter> getJSqlParserExpressionClass() {
        return JdbcParameter.class;
    }
}
