package com.jn.sqlhelper.mango;

import com.jn.sqlhelper.dialect.RowSelection;
import com.jn.sqlhelper.dialect.SQLStatementInstrumentor;

public class MangoPagingContext {
    public static final ThreadLocal<RowSelection> pagingRequest = new ThreadLocal<RowSelection>();
    public static final SQLStatementInstrumentor instrumentor = new SQLStatementInstrumentor();
}
