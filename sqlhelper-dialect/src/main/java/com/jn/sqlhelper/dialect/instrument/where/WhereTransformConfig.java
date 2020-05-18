package com.jn.sqlhelper.dialect.instrument.where;

import com.jn.langx.util.hash.HashCodeBuilder;
import com.jn.sqlhelper.dialect.expression.SQLExpression;
import com.jn.sqlhelper.dialect.instrument.InjectPosition;

public class WhereTransformConfig {

    private InjectPosition position = InjectPosition.LAST;
    private SQLExpression expression;
    private boolean instrumentSubSelect = false;

    public InjectPosition getPosition() {
        return position;
    }

    public void setPosition(InjectPosition position) {
        this.position = position;
    }

    public SQLExpression getExpression() {
        return expression;
    }

    public void setExpression(SQLExpression expression) {
        this.expression = expression;
    }

    public boolean isInstrumentSubSelect() {
        return instrumentSubSelect;
    }

    public void setInstrumentSubSelect(boolean instrumentSubSelect) {
        this.instrumentSubSelect = instrumentSubSelect;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        WhereTransformConfig that = (WhereTransformConfig) o;

        if (position != that.position) {
            return false;
        }
        if (instrumentSubSelect != that.instrumentSubSelect) {
            return false;
        }
        return expression.equals(that.expression);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().with(expression).with(position).build();
    }
}
