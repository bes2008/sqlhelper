package com.jn.sqlhelper.jsqlparser.expression;

import com.jn.sqlhelper.dialect.expression.AndExpression;
import net.sf.jsqlparser.expression.Expression;

public class AndExpressionConverter extends BinaryExpressionConverter<AndExpression, net.sf.jsqlparser.expression.operators.conditional.AndExpression> {

    @Override
    protected net.sf.jsqlparser.expression.operators.conditional.AndExpression buildJSqlParserExpression(AndExpression expression, Expression leftExp, Expression rightExp) {
        return new net.sf.jsqlparser.expression.operators.conditional.AndExpression(leftExp, rightExp);
    }

    @Override
    public Class<AndExpression> getStandardExpressionClass() {
        return AndExpression.class;
    }

    @Override
    public Class<net.sf.jsqlparser.expression.operators.conditional.AndExpression> getJSqlParserExpressionClass() {
        return net.sf.jsqlparser.expression.operators.conditional.AndExpression.class;
    }
}
