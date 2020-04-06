package com.jn.sqlhelper.dialect.sqlparser.jsqlparser.expression;

import com.jn.sqlhelper.dialect.expression.NotEqualExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;

public class NotEqualExpressionConverter extends BinaryExpressionConverter<NotEqualExpression, NotEqualsTo> {
    @Override
    protected NotEqualsTo buildJSqlParserExpression(NotEqualExpression expression, String operatorSymbol, Expression leftExp, Expression rightExp) {
        NotEqualsTo notEqualsTo = new NotEqualsTo(operatorSymbol);
        notEqualsTo.setLeftExpression(leftExp);
        notEqualsTo.setRightExpression(rightExp);
        return notEqualsTo;
    }

    @Override
    public Class<NotEqualExpression> getStandardExpressionClass() {
        return NotEqualExpression.class;
    }

    @Override
    public Class<NotEqualsTo> getJSqlParserExpressionClass() {
        return NotEqualsTo.class;
    }
}
