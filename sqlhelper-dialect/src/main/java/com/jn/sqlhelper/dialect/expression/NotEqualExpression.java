package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.el.expression.operator.compare.NE;

public class NotEqualExpression extends NE implements SQLExpression,SymbolExpression {
    public NotEqualExpression() {
        setOperateSymbol("<>");
    }
}
