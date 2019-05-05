package com.fjn.helper.sql.dialect.pagination;

import com.fjn.helper.sql.dialect.RowSelection;

public class PagingRequestContext<E, R> {
    private PagingRequest<E, R> request;
    private RowSelection rowSelection;

    public PagingRequest<E, R> getRequest() {
        return this.request;
    }

    public void setRequest(PagingRequest<E, R> request) {
        this.request = request;
    }

    public RowSelection getRowSelection() {
        return this.rowSelection;
    }

    public void setRowSelection(RowSelection rowSelection) {
        this.rowSelection = rowSelection;
    }
}
