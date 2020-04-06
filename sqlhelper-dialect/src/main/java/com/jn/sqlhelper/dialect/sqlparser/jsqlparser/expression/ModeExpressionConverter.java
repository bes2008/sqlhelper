package com.jn.sqlhelper.dialect.sqlparser.jsqlparser.expression;

import com.jn.langx.util.function.Supplier;
import com.jn.sqlhelper.dialect.expression.ModeExpression;
import net.sf.jsqlparser.expression.operators.arithmetic.Modulo;

public class ModeExpressionConverter extends BinaryExpressionConverter<ModeExpression, Modulo> {

    public ModeExpressionConverter() {
        setJsqlparserExpressionSupplier(new Supplier<ModeExpression, Modulo>() {
            @Override
            public Modulo get(ModeExpression input) {
                return new Modulo();
            }
        });
    }

    @Override
    public Class<ModeExpression> getStandardExpressionClass() {
        return ModeExpression.class;
    }

    @Override
    public Class<Modulo> getJSqlParserExpressionClass() {
        return Modulo.class;
    }
}
