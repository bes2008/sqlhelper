package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.expression.operator.compare.LT;

public class LesserThanExpression extends LT implements SQLExpression {
    public LesserThanExpression(){
        setOperateSymbol("<");
    }
}
