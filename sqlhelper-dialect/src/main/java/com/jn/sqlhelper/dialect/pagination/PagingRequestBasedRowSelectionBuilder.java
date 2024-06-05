package com.jn.sqlhelper.dialect.pagination;

import com.jn.sqlhelper.dialect.conf.Settings;

public class PagingRequestBasedRowSelectionBuilder implements RowSelectionBuilder<PagingRequest> {
    private int defaultPageSize = Settings.getInstance().getPageSize();

    @Override
    public RowSelection build(PagingRequest request)
            throws IllegalArgumentException {
        if (request.isValidRequest()) {
            RowSelection rowSelection = new RowSelection();
            rowSelection.setFetchSize(request.getFetchSize());
            rowSelection.setTimeout(request.getTimeout());

            int pageNo = request.getPageNo();
            long offset = 0L;
            int limit = request.getPageSize();
            if (request.isGetAllFromNonZeroOffsetRequest()) {
                offset = (pageNo - 1L) * getDefaultPageSize();
                limit = Integer.MAX_VALUE;
            } else {
                offset = pageNo > 0 ? (pageNo - 1L) * request.getPageSize() : 0;
            }
            rowSelection.setLimit(limit);
            if (offset - 1 + rowSelection.getLimit() > Integer.MAX_VALUE) {
                rowSelection.setFetchSize(Integer.MAX_VALUE - (int)offset);
            }
            rowSelection.setOffset(offset);

            rowSelection.setMaxRows(request.getMaxRows());
            return rowSelection;
        }
        throw new IllegalArgumentException("PagingRequest is illegal");
    }

    public int getDefaultPageSize() {
        return defaultPageSize;
    }

    public void setDefaultPageSize(int defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }
}

