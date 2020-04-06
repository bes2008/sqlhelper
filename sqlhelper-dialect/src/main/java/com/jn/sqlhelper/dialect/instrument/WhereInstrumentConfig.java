package com.jn.sqlhelper.dialect.instrument;

import com.jn.langx.util.hash.HashCodeBuilder;
import com.jn.sqlhelper.dialect.expression.SQLExpression;

public class WhereInstrumentConfig {

    public static enum Position {
        FIRST,
        LAST,
        BEST;
    }

    private Position position = Position.LAST;
    private SQLExpression expression;

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public SQLExpression getExpression() {
        return expression;
    }

    public void setExpression(SQLExpression expression) {
        this.expression = expression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        WhereInstrumentConfig that = (WhereInstrumentConfig) o;

        if (position != that.position) {
            return false;
        }
        return expression.equals(that.expression);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().with(expression).with(position).build();
    }
}
