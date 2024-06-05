package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.el.expression.BaseExpression;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.hash.HashCodeBuilder;

public class BetweenAndExpression extends BaseExpression<SQLExpression> implements SQLExpression<SQLExpression>, Notable, SymbolExpression {
    private SQLExpression target;
    private SQLExpression low;
    private SQLExpression high;
    private boolean isNotExpression = false;

    public BetweenAndExpression() {
    }

    public BetweenAndExpression(boolean isNotExpression) {
        this.isNotExpression = isNotExpression;
    }

    @Override
    public void not(boolean isNotExpression) {
        this.isNotExpression = isNotExpression;
    }

    @Override
    public boolean not() {
        return isNotExpression;
    }

    public void setTarget(SQLExpression target) {
        this.target = target;
    }

    public SQLExpression getTarget(){
        return this.target;
    }

    public void setLow(SQLExpression low) {
        this.low = low;
    }

    public SQLExpression getLow(){
        return this.low;
    }

    public void setHigh(SQLExpression high) {
        this.high = high;
    }

    public SQLExpression getHigh(){
        return this.high;
    }

    @Override
    public SQLExpression execute() {
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .with(isNotExpression)
                .with(target)
                .with(low)
                .with(high)
                .build();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj==null){
            return false;
        }
        if(obj.getClass()!= BetweenAndExpression.class){
            return false;
        }
        BetweenAndExpression that = (BetweenAndExpression)obj;
        if(!Objs.equals(this.isNotExpression, that.isNotExpression)){
            return false;
        }
        if(!Objs.equals(this.target, that.target)){
            return false;
        }
        if(!Objs.equals(this.low, that.low)){
            return false;
        }
        if(!Objs.equals(this.high, that.high)){
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        Preconditions.checkNotNull(target);
        Preconditions.checkNotNull(low);
        Preconditions.checkNotNull(high);

        StringBuilder builder = new StringBuilder(255);
        builder.append(target.toString())
                .append(not() ? " not" : "")
                .append(" between ")
                .append(low.toString())
                .append(" and ")
                .append(high.toString());
        return builder.toString();
    }
}
