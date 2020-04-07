package com.jn.sqlhelper.jsqlparser.expression;

import com.jn.sqlhelper.dialect.expression.InExpression;

public class InExpressionConverter implements ExpressionConverter<InExpression, net.sf.jsqlparser.expression.operators.relational.InExpression> {
    @Override
    public net.sf.jsqlparser.expression.operators.relational.InExpression toJSqlParserExpression(InExpression expression) {
        net.sf.jsqlparser.expression.operators.relational.InExpression exp = new net.sf.jsqlparser.expression.operators.relational.InExpression();
        exp.setNot(expression.not());
        exp.setLeftExpression(ExpressionConverters.toJSqlParserExpression(expression.getLeft()));
        exp.setRightItemsList(ExpressionConverters.toJSqlParserExpressionList(expression.getRight()));
        return exp;
    }

    @Override
    public InExpression fromJSqlParserExpression(net.sf.jsqlparser.expression.operators.relational.InExpression expression) {
        return null;
    }

    @Override
    public Class<InExpression> getStandardExpressionClass() {
        return InExpression.class;
    }

    @Override
    public Class<net.sf.jsqlparser.expression.operators.relational.InExpression> getJSqlParserExpressionClass() {
        return net.sf.jsqlparser.expression.operators.relational.InExpression.class;
    }
}
