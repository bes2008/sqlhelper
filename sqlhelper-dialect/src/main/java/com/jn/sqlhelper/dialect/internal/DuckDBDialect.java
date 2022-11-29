package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.LimitOffsetLimitHandler;

/**
 * https://duckdb.org/docs/sql/statements/select
 * https://duckdb.org/docs/api/java
 *
 * limit $count offset $offset
 */
public class DuckDBDialect extends AbstractDialect {
    public DuckDBDialect() {
        setLimitHandler(new LimitOffsetLimitHandler());
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isBindLimitParametersInReverseOrder() {
        return true;
    }

}
