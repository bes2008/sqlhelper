package com.jn.sqlhelper.dialect.sqlparser.jsqlparser.expression;

import com.jn.langx.expression.operator.BinaryOperator;
import com.jn.sqlhelper.dialect.expression.SQLExpression;
import net.sf.jsqlparser.expression.Expression;

public abstract class BinaryExpressionConverter<SE extends SQLExpression & BinaryOperator, JE extends Expression> implements ExpressionConverter<SE, JE> {
    @Override
    public JE toJSqlParserExpression(SE expression) {
        SQLExpression left = (SQLExpression) expression.getLeft();
        SQLExpression right = (SQLExpression) expression.getRight();

        ExpressionConverterRegistry registry = ExpressionConverterRegistry.getInstance();
        ExpressionConverter leftExpressionConverter = registry.getExpressionConverterByStandardExpressionClass(left.getClass());
        Expression leftExp = leftExpressionConverter.toJSqlParserExpression(left);
        ExpressionConverter rightExpressionConverter = registry.getExpressionConverterByStandardExpressionClass(right.getClass());
        Expression rightExp = rightExpressionConverter.toJSqlParserExpression(right);
        return buildJSqlParserExpression(expression, expression.getOperateSymbol(), leftExp, rightExp);
    }

    protected abstract JE buildJSqlParserExpression(SE expression, String operatorSymbol, Expression leftExp, Expression rightExp);

    @Override
    public SE fromJSqlParserExpression(JE expression) {
        return null;
    }

}
