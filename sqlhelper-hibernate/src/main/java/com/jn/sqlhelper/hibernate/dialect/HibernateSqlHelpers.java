package com.jn.sqlhelper.hibernate.dialect;

import com.jn.sqlhelper.dialect.pagination.RowSelection;

class HibernateSqlHelpers {
    public static RowSelection toSqlHelperRowSelection(org.hibernate.engine.spi.RowSelection rowSelection) {
        RowSelection rs = new RowSelection();
        rs.setMaxRows(rowSelection.getMaxRows());
        rs.setFetchSize(rowSelection.getFetchSize());
        rs.setTimeout(rowSelection.getTimeout());
        Integer firstRow = rowSelection.getFirstRow();
        if (firstRow != null) {
            rs.setOffset(firstRow.longValue());
        }
        return rs;
    }
}
