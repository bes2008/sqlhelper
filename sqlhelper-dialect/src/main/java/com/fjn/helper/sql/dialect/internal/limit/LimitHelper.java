package com.fjn.helper.sql.dialect.internal.limit;

import com.fjn.helper.sql.dialect.Dialect;
import com.fjn.helper.sql.dialect.RowSelection;

public class LimitHelper {
    public static boolean hasMaxRows(final RowSelection selection) {
        return selection != null && selection.getLimit() != null && selection.getLimit() > 0;
    }

    public static boolean useLimit(final Dialect dialect, final RowSelection selection) {
        return dialect != null && dialect.isSupportsLimit() && hasMaxRows(selection);
    }

    public static boolean hasFirstRow(final RowSelection selection) {
        return getFirstRow(selection) > 0;
    }

    public static int getFirstRow(final RowSelection selection) {
        return (selection == null || selection.getOffset() == null) ? 0 : ((int) selection.getOffset());
    }

    private LimitHelper() {
    }
}
