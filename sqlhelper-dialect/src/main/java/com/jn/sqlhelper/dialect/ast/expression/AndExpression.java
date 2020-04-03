package com.jn.sqlhelper.dialect.ast.expression;

import com.jn.langx.expression.operator.logic.And;

public class AndExpression extends And implements SQLExpression {
    public AndExpression() {
        setOperateSymbol("and");
    }
}
