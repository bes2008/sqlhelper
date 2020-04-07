package com.jn.sqlhelper.jsqlparser.expression;

import com.jn.langx.util.function.Supplier;
import com.jn.sqlhelper.dialect.expression.LesserThanExpression;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;

public class LesserThanExpressionConverter extends BinaryExpressionConverter<LesserThanExpression, MinorThan> {

    public LesserThanExpressionConverter() {
        setJsqlparserExpressionSupplier(new Supplier<LesserThanExpression, MinorThan>() {
            @Override
            public MinorThan get(LesserThanExpression input) {
                return new MinorThan();
            }
        });
    }

    @Override
    public Class<LesserThanExpression> getStandardExpressionClass() {
        return LesserThanExpression.class;
    }

    @Override
    public Class<MinorThan> getJSqlParserExpressionClass() {
        return MinorThan.class;
    }
}
