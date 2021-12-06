package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.el.expression.operator.compare.GE;

public class GreaterOrEqualExpression extends GE implements SQLExpression, SymbolExpression {
    public GreaterOrEqualExpression(){
        setOperateSymbol(">=");
    }
}
