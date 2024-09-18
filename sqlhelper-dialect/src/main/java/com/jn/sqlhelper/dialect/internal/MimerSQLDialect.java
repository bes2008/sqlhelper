package com.jn.sqlhelper.dialect.internal;

import com.jn.langx.annotation.Name;
import com.jn.sqlhelper.dialect.pagination.RowSelection;
import com.jn.sqlhelper.dialect.internal.limit.AbstractLimitHandler;

/**
 * https://download.mimer.com/pub/developer/docs/html_101/Mimer_SQL_Engine_DocSet/index.htm
 */
@Name("mimer")
public class MimerSQLDialect extends AbstractDialect {
    public MimerSQLDialect() {
        super();
        setLimitHandler(new AbstractLimitHandler() {
            @Override
            public String processSql(String sql, boolean isSubquery, boolean useLimitVariable, RowSelection rowSelection) {
                return null;
            }
        });
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return false;
    }
}
