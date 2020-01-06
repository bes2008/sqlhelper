package com.jn.sqlhelper.dialect;

import com.jn.langx.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlRequestContextHolder {
    private static final Logger logger = LoggerFactory.getLogger(SqlRequestContextHolder.class);
    protected static final ThreadLocal<SqlRequestContext> variables = new ThreadLocal<SqlRequestContext>();

    private static final SqlRequestContextHolder INSTANCE = new SqlRequestContextHolder();

    public static SqlRequestContextHolder getInstance() {
        return INSTANCE;
    }

    public SqlRequestContext get() {
        return variables.get();
    }

    public boolean isOrderByRequest() {
        SqlRequestContext ctx = get();
        return Objects.isNotNull(ctx) && ctx.isOrderByRequest();
    }
}
