package com.jn.sqlhelper.dialect.ast.expression;

import com.jn.langx.expression.operator.compare.EQ;

public class EqualExpression extends EQ implements SQLExpression {
    public EqualExpression() {
        setOperateSymbol("=");
    }
}
