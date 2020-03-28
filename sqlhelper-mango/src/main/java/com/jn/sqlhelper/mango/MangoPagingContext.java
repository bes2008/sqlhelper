package com.jn.sqlhelper.mango;

import com.jn.sqlhelper.dialect.pagination.RowSelection;
import com.jn.sqlhelper.dialect.instrument.SQLStatementInstrumentor;

public class MangoPagingContext {
    public static final ThreadLocal<RowSelection> pagingRequest = new ThreadLocal<RowSelection>();
    public static final SQLStatementInstrumentor instrumentor = new SQLStatementInstrumentor();
}
