package com.jn.sqlhelper.dialect.sqlparser.jsqlparser.expression;

import com.jn.sqlhelper.dialect.expression.BetweenAndExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;

public class BetweenAndExpressionConverter implements ExpressionConverter<BetweenAndExpression, Between> {
    @Override
    public Between toJSqlParserExpression(BetweenAndExpression expression) {
        Between between = new Between();
        between.setLeftExpression(ExpressionConverters.toJSqlParserExpression(expression.getTarget()));
        between.setNot(expression.not());
        between.setBetweenExpressionStart(ExpressionConverters.toJSqlParserExpression(expression.getLow()));
        between.setBetweenExpressionEnd(ExpressionConverters.toJSqlParserExpression(expression.getHigh()));
        return between;
    }

    @Override
    public BetweenAndExpression fromJSqlParserExpression(Between expression) {
        return null;
    }

    @Override
    public Class<BetweenAndExpression> getStandardExpressionClass() {
        return BetweenAndExpression.class;
    }

    @Override
    public Class<Between> getJSqlParserExpressionClass() {
        return Between.class;
    }
}
