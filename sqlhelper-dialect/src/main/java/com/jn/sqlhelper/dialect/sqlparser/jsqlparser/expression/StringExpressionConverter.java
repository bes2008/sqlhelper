package com.jn.sqlhelper.dialect.sqlparser.jsqlparser.expression;

import com.jn.sqlhelper.dialect.expression.StringExpression;
import net.sf.jsqlparser.expression.StringValue;

public class StringExpressionConverter implements ExpressionConverter<StringExpression, StringValue> {
    @Override
    public StringValue toJSqlParserExpression(StringExpression expression) {
        return new StringValue(expression.getValue());
    }

    @Override
    public StringExpression fromJSqlParserExpression(StringValue expression) {
        return null;
    }

    @Override
    public Class<StringExpression> getStandardExpressionClass() {
        return StringExpression.class;
    }

    @Override
    public Class<StringValue> getJSqlParserExpressionClass() {
        return StringValue.class;
    }
}
