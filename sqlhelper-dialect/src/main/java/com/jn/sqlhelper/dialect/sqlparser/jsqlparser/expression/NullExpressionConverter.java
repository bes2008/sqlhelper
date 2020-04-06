package com.jn.sqlhelper.dialect.sqlparser.jsqlparser.expression;

import com.jn.sqlhelper.dialect.expression.NullExpression;
import net.sf.jsqlparser.expression.NullValue;

public class NullExpressionConverter implements ExpressionConverter<NullExpression, NullValue>{
    @Override
    public NullValue toJSqlParserExpression(NullExpression expression) {
        return new NullValue();
    }

    @Override
    public NullExpression fromJSqlParserExpression(NullValue expression) {
        return null;
    }

    @Override
    public Class<NullExpression> getStandardExpressionClass() {
        return NullExpression.class;
    }

    @Override
    public Class<NullValue> getJSqlParserExpressionClass() {
        return NullValue.class;
    }
}
