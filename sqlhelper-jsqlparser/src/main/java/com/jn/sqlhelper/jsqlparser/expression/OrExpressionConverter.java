package com.jn.sqlhelper.jsqlparser.expression;

import com.jn.sqlhelper.dialect.expression.OrExpression;
import net.sf.jsqlparser.expression.Expression;

public class OrExpressionConverter extends BinaryExpressionConverter<OrExpression, net.sf.jsqlparser.expression.operators.conditional.OrExpression> {
    @Override
    protected net.sf.jsqlparser.expression.operators.conditional.OrExpression buildJSqlParserExpression(OrExpression expression, Expression leftExp, Expression rightExp) {
        return new net.sf.jsqlparser.expression.operators.conditional.OrExpression(leftExp, rightExp);
    }

    @Override
    public Class<OrExpression> getStandardExpressionClass() {
        return OrExpression.class;
    }

    @Override
    public Class<net.sf.jsqlparser.expression.operators.conditional.OrExpression> getJSqlParserExpressionClass() {
        return net.sf.jsqlparser.expression.operators.conditional.OrExpression.class;
    }
}
