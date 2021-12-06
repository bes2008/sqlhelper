package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.el.expression.operator.compare.LT;

public class LesserThanExpression extends LT implements SQLExpression, SymbolExpression {
    public LesserThanExpression(){
        setOperateSymbol("<");
    }
}
