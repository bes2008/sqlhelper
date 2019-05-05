package com.fjn.helper.sql.dialect.pagination;

import com.fjn.helper.sql.dialect.RowSelection;
import com.fjn.helper.sql.dialect.RowSelectionBuilder;

public class PagingRequestBasedRowSelectionBuilder implements RowSelectionBuilder<PagingRequest> {
    @Override
    public RowSelection build(PagingRequest request)
            throws IllegalArgumentException {
        if (request.isValidRequest()) {
            RowSelection rowSelection = new RowSelection();
            rowSelection.setFetchSize(request.getFetchSize());
            rowSelection.setTimeout(request.getTimeout());
            rowSelection.setLimit(request.getPageSize());
            int pageNo = request.getPageNo();
            int offset = pageNo > 0 ? (pageNo - 1) * request.getPageSize() : 0;
            rowSelection.setOffset(offset);
            return rowSelection;
        }
        throw new IllegalArgumentException("PagingRequest is illegal");
    }
}

