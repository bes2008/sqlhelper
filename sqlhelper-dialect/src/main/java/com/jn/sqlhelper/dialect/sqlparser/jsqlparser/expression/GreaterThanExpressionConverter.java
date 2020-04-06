package com.jn.sqlhelper.dialect.sqlparser.jsqlparser.expression;

import com.jn.langx.util.function.Supplier;
import com.jn.sqlhelper.dialect.expression.GreaterThanExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;

public class GreaterThanExpressionConverter extends BinaryExpressionConverter<GreaterThanExpression, GreaterThan> {
    public GreaterThanExpressionConverter(){
        setJsqlparserExpressionSupplier(new Supplier<GreaterThanExpression, GreaterThan>() {
            @Override
            public GreaterThan get(GreaterThanExpression input) {
                return new GreaterThan();
            }
        });
    }

    @Override
    public Class<GreaterThanExpression> getStandardExpressionClass() {
        return GreaterThanExpression.class;
    }

    @Override
    public Class<GreaterThan> getJSqlParserExpressionClass() {
        return GreaterThan.class;
    }
}
