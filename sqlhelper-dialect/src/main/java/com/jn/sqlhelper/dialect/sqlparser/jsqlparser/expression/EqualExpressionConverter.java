package com.jn.sqlhelper.dialect.sqlparser.jsqlparser.expression;

import com.jn.sqlhelper.dialect.expression.EqualExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;

public class EqualExpressionConverter extends BinaryExpressionConverter<EqualExpression, EqualsTo> {

    @Override
    protected EqualsTo buildJSqlParserExpression(EqualExpression expression, String operatorSymbol, Expression leftExp, Expression rightExp) {
        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(leftExp);
        equalsTo.setRightExpression(rightExp);
        return equalsTo;
    }

    @Override
    public Class<EqualExpression> getStandardExpressionClass() {
        return EqualExpression.class;
    }

    @Override
    public Class<EqualsTo> getJSqlParserExpressionClass() {
        return EqualsTo.class;
    }
}
