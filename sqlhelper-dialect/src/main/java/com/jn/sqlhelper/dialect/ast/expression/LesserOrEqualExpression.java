package com.jn.sqlhelper.dialect.ast.expression;

import com.jn.langx.expression.operator.compare.LE;

public class LesserOrEqualExpression extends LE implements SQLExpression {
    public LesserOrEqualExpression(){
        setOperateSymbol("<=");
    }
}
