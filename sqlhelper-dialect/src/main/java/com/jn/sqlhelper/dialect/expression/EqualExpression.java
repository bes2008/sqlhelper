package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.el.expression.operator.compare.EQ;

public class EqualExpression extends EQ implements SQLExpression, SymbolExpression {
    public EqualExpression() {
        setOperateSymbol("=");
    }
}
