package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.el.expression.operator.compare.GT;

public class GreaterThanExpression extends GT implements SQLExpression, SymbolExpression {
    public GreaterThanExpression() {
        setOperateSymbol(">");
    }

}
