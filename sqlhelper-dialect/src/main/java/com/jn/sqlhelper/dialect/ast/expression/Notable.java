package com.jn.sqlhelper.dialect.ast.expression;

public interface Notable {
    void not(boolean isNotExpression);
    boolean not();
}
