package com.jn.sqlhelper.dialect.ast.expression;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;

import java.util.List;

public class ListExpression implements SQLExpression<SQLExpression> {
    private final List<SQLExpression> expressions = Collects.emptyArrayList();

    @Override
    public SQLExpression execute() {
        return null;
    }

    public void addAll(List<SQLExpression> expressions) {
        for (SQLExpression expression : expressions) {
            add(expression);
        }
    }

    public void add(SQLExpression expression) {
        expressions.add(expression);
    }

    public boolean isEmpty() {
        return expressions.isEmpty();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ListExpression)) {
            return false;
        }
        ListExpression that = (ListExpression) obj;
        if (expressions.size() != that.expressions.size()) {
            return false;
        }
        if (!Collects.containsAll(expressions, that.expressions)) {
            return false;
        }

        if (!Collects.containsAll(that.expressions, expressions)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return expressions.hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(255);
        Collects.forEach(expressions, new Consumer2<Integer, SQLExpression>() {
            @Override
            public void accept(Integer index, SQLExpression expression) {
                if (index > 0) {
                    builder.append(", ");
                }
                builder.append(expression.toString());
            }
        });
        return builder.toString();
    }
}
