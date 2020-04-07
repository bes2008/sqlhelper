package com.jn.sqlhelper.jsqlparser.expression;

import com.jn.langx.util.function.Supplier;
import com.jn.sqlhelper.dialect.expression.GreaterOrEqualExpression;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;

public class GreaterOrEqualExpressionConverter extends BinaryExpressionConverter<GreaterOrEqualExpression, GreaterThanEquals> {
    public GreaterOrEqualExpressionConverter() {
        setJsqlparserExpressionSupplier(new Supplier<GreaterOrEqualExpression, GreaterThanEquals>() {
            @Override
            public GreaterThanEquals get(GreaterOrEqualExpression input) {
                return new GreaterThanEquals();
            }
        });
    }

    @Override
    public Class<GreaterOrEqualExpression> getStandardExpressionClass() {
        return GreaterOrEqualExpression.class;
    }

    @Override
    public Class<GreaterThanEquals> getJSqlParserExpressionClass() {
        return GreaterThanEquals.class;
    }
}
