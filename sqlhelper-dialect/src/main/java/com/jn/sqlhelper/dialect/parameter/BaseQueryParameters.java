
package com.jn.sqlhelper.dialect.parameter;

import com.jn.langx.util.collection.Collects;
import com.jn.sqlhelper.dialect.pagination.QueryParameters;
import com.jn.sqlhelper.dialect.pagination.RowSelection;

import java.util.Map;

public class BaseQueryParameters<P> implements QueryParameters<P> {
    private RowSelection rowSelection;
    private boolean isCallable;
    protected P parameters;
    private int beforeSubqueryCount = 0;
    private int afterSubqueryCount = 0;
    protected P beforeSubqueryParameters;
    protected P subqueryParameters;
    protected P afterSubqueryParameters;

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
        return Collects.count(parameters);
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

    /**
     * invoke it when a paging request
     */
    public void setParameters(P parameters) {
        this.parameters = parameters;
    }

    /**
     * invoke it when a subquery  paging request
     */
    public void setParameters(P parameters, int beforeSubqueryCount, int afterSubqueryCount) {
        this.parameters = parameters;
        this.beforeSubqueryCount = beforeSubqueryCount;
        this.afterSubqueryCount = afterSubqueryCount;
    }

    @Override
    public int getBeforeSubqueryParameterCount() {
        return beforeSubqueryCount;
    }

    @Override
    public int getAfterSubqueryParameterCount() {
        return afterSubqueryCount;
    }

    @Override
    public P getBeforeSubqueryParameterValues() {
        return beforeSubqueryParameters;
    }

    @Override
    public P getAfterSubqueryParameterValues() {
        return afterSubqueryParameters;
    }

    @Override
    public P getSubqueryParameterValues() {
        return subqueryParameters;
    }
}