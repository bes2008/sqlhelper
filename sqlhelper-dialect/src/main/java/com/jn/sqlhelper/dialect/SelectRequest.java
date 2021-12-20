package com.jn.sqlhelper.dialect;

import com.jn.langx.util.Objs;
import com.jn.sqlhelper.dialect.orderby.OrderBy;

public class SelectRequest<R extends SelectRequest, C extends SqlRequestContext<R>> extends SqlRequest<R, C> {
    private OrderBy orderBy;
    private int timeout;

    public String getOrderByAsString() {
        return Objs.isNull(this.orderBy) ? "" : this.orderBy.toString();
    }

    public OrderBy getOrderBy() {
        return orderBy;
    }

    public SelectRequest<R, C> setOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public boolean needOrderBy() {
        if (this.orderBy == null || !this.orderBy.isValid()) {
            return false;
        }
        return true;
    }


    public int getTimeout() {
        return this.timeout;
    }

    public SelectRequest<R, C> setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }
}
