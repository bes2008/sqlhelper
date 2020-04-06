package com.jn.sqlhelper.dialect.sqlparser.jsqlparser.expression;

import com.jn.sqlhelper.dialect.expression.GreaterThanExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;

public class GreaterThanExpressionConverter extends BinaryExpressionConverter<GreaterThanExpression, GreaterThan> {
    @Override
    protected GreaterThan buildJSqlParserExpression(GreaterThanExpression expression, String operatorSymbol, Expression leftExp, Expression rightExp) {
        GreaterThan gt = new GreaterThan();
        gt.setLeftExpression(leftExp);
        gt.setRightExpression(rightExp);
        return gt;
    }

    @Override
    public Class<GreaterThanExpression> getStandardExpressionClass() {
        return GreaterThanExpression.class;
    }

    @Override
    public Class<GreaterThan> getJSqlParserExpressionClass() {
        return GreaterThan.class;
    }
}
