package com.jn.sqlhelper.dialect.sqlparser.jsqlparser.expression;

import com.jn.langx.util.function.Supplier;
import com.jn.sqlhelper.dialect.expression.MultipleExpression;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;

public class MultipleExpressionConverter extends BinaryExpressionConverter<MultipleExpression, Multiplication> {

    public MultipleExpressionConverter() {
        setJsqlparserExpressionSupplier(new Supplier<MultipleExpression, Multiplication>() {
            @Override
            public Multiplication get(MultipleExpression input) {
                return new Multiplication();
            }
        });
    }

    @Override
    public Class<MultipleExpression> getStandardExpressionClass() {
        return MultipleExpression.class;
    }

    @Override
    public Class<Multiplication> getJSqlParserExpressionClass() {
        return Multiplication.class;
    }
}
