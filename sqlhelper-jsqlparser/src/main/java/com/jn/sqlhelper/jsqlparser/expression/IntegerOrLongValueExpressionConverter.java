package com.jn.sqlhelper.jsqlparser.expression;

import com.jn.sqlhelper.dialect.expression.IntegerOrLongExpression;
import net.sf.jsqlparser.expression.LongValue;

public class IntegerOrLongValueExpressionConverter implements ExpressionConverter<IntegerOrLongExpression, LongValue> {
    @Override
    public LongValue toJSqlParserExpression(IntegerOrLongExpression expression) {
        return new LongValue(expression.getValue());
    }

    @Override
    public IntegerOrLongExpression fromJSqlParserExpression(LongValue expression) {
        return null;
    }

    @Override
    public Class<IntegerOrLongExpression> getStandardExpressionClass() {
        return IntegerOrLongExpression.class;
    }

    @Override
    public Class<LongValue> getJSqlParserExpressionClass() {
        return LongValue.class;
    }
}
