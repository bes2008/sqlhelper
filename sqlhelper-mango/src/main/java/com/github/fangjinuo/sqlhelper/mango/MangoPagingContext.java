package com.github.fangjinuo.sqlhelper.mango;

import com.github.fangjinuo.sqlhelper.dialect.RowSelection;
import com.github.fangjinuo.sqlhelper.dialect.SQLStatementInstrumentor;

public class MangoPagingContext {
    public static final ThreadLocal<RowSelection> pagingRequest = new ThreadLocal<RowSelection>();
    public static final SQLStatementInstrumentor instrumentor = new SQLStatementInstrumentor();
}
