package com.jn.sqlhelper.dialect.sqlparser.jsqlparser.expression;

import com.jn.sqlhelper.dialect.expression.GreaterOrEqualExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;

public class GreaterThanOrEqualExpressionConverter extends BinaryExpressionConverter<GreaterOrEqualExpression, GreaterThanEquals> {
    @Override
    protected GreaterThanEquals buildJSqlParserExpression(GreaterOrEqualExpression expression, String operatorSymbol, Expression leftExp, Expression rightExp) {
        GreaterThanEquals ge = new GreaterThanEquals();
        ge.setLeftExpression(leftExp);
        ge.setRightExpression(rightExp);
        return ge;
    }

    @Override
    public Class<GreaterOrEqualExpression> getStandardExpressionClass() {
        return GreaterOrEqualExpression.class;
    }

    @Override
    public Class<GreaterThanEquals> getJSqlParserExpressionClass() {
        return GreaterThanEquals.class;
    }
}
