package com.jn.sqlhelper.dialect.ast.expression;

import com.jn.langx.expression.operator.compare.GE;

public class GreaterOrEqualExpression extends GE implements SQLExpression {
    public GreaterOrEqualExpression(){
        setOperateSymbol(">=");
    }
}
