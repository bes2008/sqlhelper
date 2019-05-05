package com.fjn.helper.sql.dialect.parameter;

import com.fjn.helper.sql.dialect.QueryParameters;
import com.fjn.helper.sql.dialect.RowSelection;
import com.fjn.helper.sql.util.Holder;

import java.util.Collection;
import java.util.Map;

public class BaseQueryParameters<P> implements QueryParameters<P> {
    private RowSelection rowSelection;
    private boolean isCallable;
    protected P parameters;

    @Override
    public P getParameterValues() {
        return parameters;
    }

    @Override
    public int getParameterValuesSize() {
        if (parameters == null) {
            return 0;
        }
        if (parameters instanceof Map) {
            return ((Map) parameters).size();
        }
        if (parameters instanceof Collection) {
            return ((Collection) parameters).size();
        }
        if (parameters.getClass().isArray()) {
            return ((Object[]) parameters).length;
        }
        if (parameters instanceof Iterable) {
            Holder<Integer> count = new Holder<>();
            ((Iterable) parameters).forEach(parameter -> count.set(count.get() + 1));
            return count.get();
        }
        return 1;
    }

    @Override
    public RowSelection getRowSelection() {
        return rowSelection;
    }

    public void setRowSelection(RowSelection rowSelection) {
        this.rowSelection = rowSelection;
    }

    @Override
    public boolean isCallable() {
        return isCallable;
    }

    public void setCallable(boolean callable) {
        isCallable = callable;
    }

    public void setParameters(P parameters) {
        this.parameters = parameters;
    }
}