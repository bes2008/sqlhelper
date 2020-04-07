package com.jn.sqlhelper.jsqlparser.expression;

import com.jn.langx.util.function.Supplier;
import com.jn.sqlhelper.dialect.expression.DivideExpression;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;

public class DivideExpressionConverter extends BinaryExpressionConverter<DivideExpression, Division> {
    public DivideExpressionConverter() {
        setJsqlparserExpressionSupplier(new Supplier<DivideExpression, Division>() {
            @Override
            public Division get(DivideExpression input) {
                return new Division();
            }
        });
    }

    @Override
    public Class<DivideExpression> getStandardExpressionClass() {
        return null;
    }

    @Override
    public Class<Division> getJSqlParserExpressionClass() {
        return null;
    }
}
