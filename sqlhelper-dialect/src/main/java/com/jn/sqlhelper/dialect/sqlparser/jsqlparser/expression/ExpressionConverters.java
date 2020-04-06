package com.jn.sqlhelper.dialect.sqlparser.jsqlparser.expression;

import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.sqlhelper.dialect.expression.ListExpression;
import com.jn.sqlhelper.dialect.expression.SQLExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;

import java.util.List;

public class ExpressionConverters {

    public static Expression toJSqlParserExpression(SQLExpression expression) {
        ExpressionConverterRegistry registry = ExpressionConverterRegistry.getInstance();
        ExpressionConverter converter = registry.getExpressionConverterByStandardExpressionClass(expression.getClass());
        return converter.toJSqlParserExpression(expression);
    }

    public static ExpressionList toJSqlParserExpressionList(final ListExpression expression) {
        List<Expression> expressions = Pipeline.of(expression.getExpressions()).map(new Function<SQLExpression, Expression>() {
            @Override
            public Expression apply(SQLExpression input) {
                return toJSqlParserExpression(expression);
            }
        }).asList();
        ExpressionList result = new ExpressionList();
        result.setExpressions(expressions);
        return result;
    }

    public final SQLExpression fromJSqlParserExpression(Expression expression) {
        ExpressionConverterRegistry registry = ExpressionConverterRegistry.getInstance();
        ExpressionConverter converter = registry.getExpressionConverterByJSqlParserExpressionClass(expression.getClass());
        return converter.fromJSqlParserExpression(expression);
    }

}
