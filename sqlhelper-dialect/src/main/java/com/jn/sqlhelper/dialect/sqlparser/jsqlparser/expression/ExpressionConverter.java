package com.jn.sqlhelper.dialect.sqlparser.jsqlparser.expression;

import com.jn.langx.annotation.NonNull;
import com.jn.sqlhelper.dialect.expression.SQLExpression;
import net.sf.jsqlparser.expression.Expression;

public interface ExpressionConverter<SE extends SQLExpression, JE extends Expression> {
    JE toJSqlParserExpression(@NonNull SE expression);
    SE fromJSqlParserExpression(@NonNull JE expression);

    Class<SE> getStandardExpressionClass();
    Class<JE> getJSqlParserExpressionClass();
}
