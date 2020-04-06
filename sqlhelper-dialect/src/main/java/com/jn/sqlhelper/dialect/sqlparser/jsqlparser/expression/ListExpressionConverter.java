package com.jn.sqlhelper.dialect.sqlparser.jsqlparser.expression;

import com.jn.sqlhelper.dialect.expression.ListExpression;
import net.sf.jsqlparser.expression.ValueListExpression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;

public class ListExpressionConverter implements ExpressionConverter<ListExpression, ValueListExpression> {
    @Override
    public ValueListExpression toJSqlParserExpression(ListExpression expression) {
        ExpressionList expressionList = ExpressionConverters.toJSqlParserExpressionList(expression);
        ValueListExpression valueListExpression = new ValueListExpression();
        valueListExpression.setExpressionList(expressionList);
        return valueListExpression;
    }

    @Override
    public ListExpression fromJSqlParserExpression(ValueListExpression expression) {
        return null;
    }

    @Override
    public Class<ListExpression> getStandardExpressionClass() {
        return ListExpression.class;
    }

    @Override
    public Class<ValueListExpression> getJSqlParserExpressionClass() {
        return ValueListExpression.class;
    }
}
