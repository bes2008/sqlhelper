package com.jn.sqlhelper.dialect;

import com.jn.langx.util.Objects;
import com.jn.langx.util.function.Consumer;
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
    public void setSqlRequest(final SqlRequest request) {
        setContextContent(new Consumer<SqlRequestContext>() {
            @Override
            public void accept(SqlRequestContext context) {
                context.setRequest(request);
                request.setContext(context);
            }
        });
    }
    private <X> void setContextContent(Consumer<SqlRequestContext> consumer) {
        SqlRequestContext context = get();
        try {
            if (Objects.isNull(context)) {
                context = SqlRequestContext.class.newInstance();
                if (Objects.isNull(context)) {
                    variables.remove();
                }
            }
            if (Objects.isNotNull(context)) {
                variables.set(context);
                consumer.accept(context);
            }
        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public boolean isTenantRequest() {
        SqlRequestContext ctx = get();
        return Objects.isNotNull(ctx) && ctx.isTenantRequest();
    }
    public void clear(){
        SqlRequestContext requestContext = get();
        if(Objects.isNotNull(requestContext)){
            requestContext.clear();
        }
        variables.remove();
    }
}
