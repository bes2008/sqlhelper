package com.jn.sqlhelper.dialect.sqlparser.jsqlparser.expression;

import com.jn.langx.util.function.Supplier;
import com.jn.sqlhelper.dialect.expression.NotEqualExpression;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;

public class NotEqualExpressionConverter extends BinaryExpressionConverter<NotEqualExpression, NotEqualsTo> {
    public NotEqualExpressionConverter() {
        setJsqlparserExpressionSupplier(new Supplier<NotEqualExpression, NotEqualsTo>() {
            @Override
            public NotEqualsTo get(NotEqualExpression input) {
                return new NotEqualsTo(input.getOperateSymbol());
            }
        });
    }


    @Override
    public Class<NotEqualExpression> getStandardExpressionClass() {
        return NotEqualExpression.class;
    }

    @Override
    public Class<NotEqualsTo> getJSqlParserExpressionClass() {
        return NotEqualsTo.class;
    }
}
