package com.jn.sqlhelper.dialect.sqlparser.jsqlparser.expression;

import com.jn.sqlhelper.dialect.expression.IsNullExpression;

public class IsNullExpressionConverter implements ExpressionConverter<IsNullExpression, net.sf.jsqlparser.expression.operators.relational.IsNullExpression> {
    @Override
    public net.sf.jsqlparser.expression.operators.relational.IsNullExpression toJSqlParserExpression(IsNullExpression expression) {
        net.sf.jsqlparser.expression.operators.relational.IsNullExpression exp = new net.sf.jsqlparser.expression.operators.relational.IsNullExpression();
        exp.setNot(expression.not());
        exp.setLeftExpression(ExpressionConverters.toJSqlParserExpression(expression.getTarget()));
        return exp;
    }

    @Override
    public IsNullExpression fromJSqlParserExpression(net.sf.jsqlparser.expression.operators.relational.IsNullExpression expression) {
        return null;
    }

    @Override
    public Class<IsNullExpression> getStandardExpressionClass() {
        return IsNullExpression.class;
    }

    @Override
    public Class<net.sf.jsqlparser.expression.operators.relational.IsNullExpression> getJSqlParserExpressionClass() {
        return net.sf.jsqlparser.expression.operators.relational.IsNullExpression.class;
    }
}
