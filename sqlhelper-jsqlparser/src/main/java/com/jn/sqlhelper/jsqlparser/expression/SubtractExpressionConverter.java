package com.jn.sqlhelper.jsqlparser.expression;

import com.jn.langx.util.function.Supplier;
import com.jn.sqlhelper.dialect.expression.SubtractExpression;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;

public class SubtractExpressionConverter extends BinaryExpressionConverter<SubtractExpression, Subtraction> {
    public SubtractExpressionConverter() {
        setJsqlparserExpressionSupplier(new Supplier<SubtractExpression, Subtraction>() {
            @Override
            public Subtraction get(SubtractExpression input) {
                return new Subtraction();
            }
        });
    }

    @Override
    public Class<SubtractExpression> getStandardExpressionClass() {
        return SubtractExpression.class;
    }

    @Override
    public Class<Subtraction> getJSqlParserExpressionClass() {
        return Subtraction.class;
    }
}
