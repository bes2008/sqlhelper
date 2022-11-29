package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.LimitOffsetLimitHandler;

/**
 * https://docs.databricks.com/sql/language-manual/sql-ref-syntax-qry-select-offset.html
 *
 *
 * LIMIT $count OFFSET $offset;
 */
public class DatabricksDialect extends AbstractDialect {
    public DatabricksDialect() {
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
