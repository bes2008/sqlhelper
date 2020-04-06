package com.jn.sqlhelper.dialect.sqlparser.jsqlparser.expression;

import com.jn.sqlhelper.dialect.expression.SQLExpression;
import net.sf.jsqlparser.expression.Expression;

public class ExpressionConverters {

    public static Expression toJSqlParserExpression(SQLExpression expression) {
        ExpressionConverterRegistry registry = ExpressionConverterRegistry.getInstance();
        ExpressionConverter converter = registry.getExpressionConverterByStandardExpressionClass(expression.getClass());
        return converter.toJSqlParserExpression(expression);
    }

    public final SQLExpression fromJSqlParserExpression(Expression expression) {
        ExpressionConverterRegistry registry = ExpressionConverterRegistry.getInstance();
        ExpressionConverter converter = registry.getExpressionConverterByJSqlParserExpressionClass(expression.getClass());
        return converter.fromJSqlParserExpression(expression);
    }

}
