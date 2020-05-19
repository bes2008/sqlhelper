package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.expression.operator.logic.Non;

public class NotExpression extends Non implements SQLExpression , SymbolExpression {
    public NotExpression() {
        setOperateSymbol("not");
    }


    @Override
    public String toString() {
        return getOperateSymbol() + " " + getTarget().toString();
    }
}
