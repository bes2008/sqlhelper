package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.expression.operator.compare.GT;

public class GreaterThanExpression extends GT implements SQLExpression {
    public GreaterThanExpression() {
        setOperateSymbol(">");
    }

}
