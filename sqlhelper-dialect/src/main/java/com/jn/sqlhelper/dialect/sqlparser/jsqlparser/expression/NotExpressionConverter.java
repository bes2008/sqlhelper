package com.jn.sqlhelper.dialect.sqlparser.jsqlparser.expression;

import com.jn.sqlhelper.dialect.expression.NotExpression;
import com.jn.sqlhelper.dialect.expression.SQLExpression;

public class NotExpressionConverter implements ExpressionConverter<NotExpression, net.sf.jsqlparser.expression.NotExpression> {
    @Override
    public net.sf.jsqlparser.expression.NotExpression toJSqlParserExpression(NotExpression expression) {
        return new net.sf.jsqlparser.expression.NotExpression(ExpressionConverters.toJSqlParserExpression((SQLExpression) expression.getTarget()));
    }

    @Override
    public NotExpression fromJSqlParserExpression(net.sf.jsqlparser.expression.NotExpression expression) {
        return null;
    }

    @Override
    public Class<NotExpression> getStandardExpressionClass() {
        return NotExpression.class;
    }

    @Override
    public Class<net.sf.jsqlparser.expression.NotExpression> getJSqlParserExpressionClass() {
        return net.sf.jsqlparser.expression.NotExpression.class;
    }
}
