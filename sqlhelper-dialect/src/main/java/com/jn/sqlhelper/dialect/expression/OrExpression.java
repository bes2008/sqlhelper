package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.el.expression.operator.logic.OR;

public class OrExpression extends OR implements SQLExpression, SymbolExpression {
    public OrExpression() {
        setOperateSymbol("or");
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
