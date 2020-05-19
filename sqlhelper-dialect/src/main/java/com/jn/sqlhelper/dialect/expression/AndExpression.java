package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.expression.operator.logic.And;

public class AndExpression extends And implements SQLExpression ,SymbolExpression{
    public AndExpression() {
        setOperateSymbol("and");
    }
}
