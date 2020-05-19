package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.expression.operator.compare.LE;

public class LesserOrEqualExpression extends LE implements SQLExpression, SymbolExpression {
    public LesserOrEqualExpression(){
        setOperateSymbol("<=");
    }
}
