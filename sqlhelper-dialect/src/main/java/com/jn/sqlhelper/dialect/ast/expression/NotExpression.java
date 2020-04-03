package com.jn.sqlhelper.dialect.ast.expression;

import com.jn.langx.expression.operator.logic.Non;

public class NotExpression extends Non implements SQLExpression {
    public NotExpression() {
        setOperateSymbol("not");
    }


    @Override
    public String toString() {
        return getOperateSymbol() + " " + getTarget().toString();
    }
}
