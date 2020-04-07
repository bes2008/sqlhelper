package com.jn.sqlhelper.jsqlparser.expression;

import com.jn.langx.util.function.Supplier;
import com.jn.sqlhelper.dialect.expression.AddExpression;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;

public class AddExpressionConverter extends BinaryExpressionConverter<AddExpression, Addition> {
    public AddExpressionConverter() {
        setJsqlparserExpressionSupplier(new Supplier<AddExpression, Addition>() {
            @Override
            public Addition get(AddExpression input) {
                return new Addition();
            }
        });
    }

    @Override
    public Class<AddExpression> getStandardExpressionClass() {
        return AddExpression.class;
    }

    @Override
    public Class<Addition> getJSqlParserExpressionClass() {
        return Addition.class;
    }
}
