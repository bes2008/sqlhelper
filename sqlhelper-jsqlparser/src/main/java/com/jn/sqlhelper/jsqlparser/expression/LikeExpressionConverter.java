package com.jn.sqlhelper.jsqlparser.expression;

import com.jn.sqlhelper.dialect.expression.LikeExpression;
import net.sf.jsqlparser.expression.Expression;

public class LikeExpressionConverter extends BinaryExpressionConverter<LikeExpression, net.sf.jsqlparser.expression.operators.relational.LikeExpression> {
    @Override
    protected net.sf.jsqlparser.expression.operators.relational.LikeExpression buildJSqlParserExpression(LikeExpression expression, Expression leftExp, Expression rightExp) {
        net.sf.jsqlparser.expression.operators.relational.LikeExpression like = new net.sf.jsqlparser.expression.operators.relational.LikeExpression();
        like.setLeftExpression(ExpressionConverters.toJSqlParserExpression(expression.getLeft()));
        like.setRightExpression(ExpressionConverters.toJSqlParserExpression(expression.getRight()));
        like.setCaseInsensitive(expression.isCaseInsensitive());
        like.setEscape("" + expression.getEscape());
        like.setNot(expression.not());
        return like;
    }

    @Override
    public Class<LikeExpression> getStandardExpressionClass() {
        return LikeExpression.class;
    }

    @Override
    public Class<net.sf.jsqlparser.expression.operators.relational.LikeExpression> getJSqlParserExpressionClass() {
        return net.sf.jsqlparser.expression.operators.relational.LikeExpression.class;
    }
}
