package com.jn.sqlhelper.dialect.sqlparser.jsqlparser.expression;

import com.jn.langx.util.function.Supplier;
import com.jn.sqlhelper.dialect.expression.LesserOrEqualExpression;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;

public class LesserOrEqualExpressionConverter extends BinaryExpressionConverter<LesserOrEqualExpression, MinorThanEquals> {

    public LesserOrEqualExpressionConverter(){
        setJsqlparserExpressionSupplier(new Supplier<LesserOrEqualExpression, MinorThanEquals>() {
            @Override
            public MinorThanEquals get(LesserOrEqualExpression input) {
                return new MinorThanEquals();
            }
        });
    }

    @Override
    public Class<LesserOrEqualExpression> getStandardExpressionClass() {
        return LesserOrEqualExpression.class;
    }

    @Override
    public Class<MinorThanEquals> getJSqlParserExpressionClass() {
        return MinorThanEquals.class;
    }
}
