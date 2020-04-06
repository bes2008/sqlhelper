package com.jn.sqlhelper.dialect.sqlparser.jsqlparser.expression;

import com.jn.sqlhelper.dialect.expression.ExistsExpression;

public class ExistsExpressionConverter implements ExpressionConverter<ExistsExpression, net.sf.jsqlparser.expression.operators.relational.ExistsExpression> {
    @Override
    public net.sf.jsqlparser.expression.operators.relational.ExistsExpression toJSqlParserExpression(ExistsExpression expression) {
        net.sf.jsqlparser.expression.operators.relational.ExistsExpression exp = new net.sf.jsqlparser.expression.operators.relational.ExistsExpression();
        exp.setNot(expression.not());
        exp.setRightExpression(ExpressionConverters.toJSqlParserExpression(expression.getTarget()));
        return exp;
    }

    @Override
    public ExistsExpression fromJSqlParserExpression(net.sf.jsqlparser.expression.operators.relational.ExistsExpression expression) {
        return null;
    }

    @Override
    public Class<ExistsExpression> getStandardExpressionClass() {
        return ExistsExpression.class;
    }

    @Override
    public Class<net.sf.jsqlparser.expression.operators.relational.ExistsExpression> getJSqlParserExpressionClass() {
        return net.sf.jsqlparser.expression.operators.relational.ExistsExpression.class;
    }
}
