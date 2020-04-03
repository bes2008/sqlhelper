package com.jn.sqlhelper.dialect.ast.expression;

import com.jn.langx.expression.operator.logic.OR;

public class OrExpression extends OR implements SQLExpression {
    public OrExpression(){
        setOperateSymbol("or");
    }
}
