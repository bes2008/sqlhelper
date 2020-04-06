package com.jn.sqlhelper.dialect.sqlparser.jsqlparser.expression;

import com.jn.sqlhelper.dialect.expression.DoubleExpression;
import net.sf.jsqlparser.expression.DoubleValue;

public class DoubleExpressionConverter implements ExpressionConverter<DoubleExpression, DoubleValue>{
    @Override
    public DoubleValue toJSqlParserExpression(DoubleExpression expression) {
        return new DoubleValue(expression.toString());
    }

    @Override
    public DoubleExpression fromJSqlParserExpression(DoubleValue expression) {
        return null;
    }

    @Override
    public Class<DoubleExpression> getStandardExpressionClass() {
        return DoubleExpression.class;
    }

    @Override
    public Class<DoubleValue> getJSqlParserExpressionClass() {
        return DoubleValue.class;
    }
}
