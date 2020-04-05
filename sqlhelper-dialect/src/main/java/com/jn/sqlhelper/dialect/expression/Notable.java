package com.jn.sqlhelper.dialect.expression;

public interface Notable {
    void not(boolean isNotExpression);
    boolean not();
}
