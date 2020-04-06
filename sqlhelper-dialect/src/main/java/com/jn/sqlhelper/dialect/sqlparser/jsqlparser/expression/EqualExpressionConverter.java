package com.jn.sqlhelper.dialect.sqlparser.jsqlparser.expression;

import com.jn.langx.util.function.Supplier;
import com.jn.sqlhelper.dialect.expression.EqualExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;

public class EqualExpressionConverter extends BinaryExpressionConverter<EqualExpression, EqualsTo> {

    public EqualExpressionConverter(){
        setJsqlparserExpressionSupplier(new Supplier<EqualExpression, EqualsTo>() {
            @Override
            public EqualsTo get(EqualExpression input) {
                return new EqualsTo();
            }
        });
    }

    @Override
    public Class<EqualExpression> getStandardExpressionClass() {
        return EqualExpression.class;
    }

    @Override
    public Class<EqualsTo> getJSqlParserExpressionClass() {
        return EqualsTo.class;
    }
}
