package com.fjn.helper.sql.dialect;

public interface RowSelectionBuilder<T>
{
    RowSelection build(final T p0);
}
