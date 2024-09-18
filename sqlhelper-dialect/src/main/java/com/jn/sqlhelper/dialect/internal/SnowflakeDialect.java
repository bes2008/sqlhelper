package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.OffsetFetchFirstOnlyLimitHandler;

/**
 * https://docs.snowflake.net/manuals/sql-reference/constructs/limit.html
 * <p>
 * support two limit syntax:
 * SELECT ...
 * FROM ...
 * [ ORDER BY ... ]
 * LIMIT <count> [ OFFSET <start> ]
 * [ ... ]
 * <p>
 * -- ANSI syntax
 * SELECT ...
 * FROM ...
 * [ ORDER BY ... ]
 * [ OFFSET <start> ] [ { ROW | ROWS } ] FETCH [ { FIRST | NEXT } ] <count> [ { ROW | ROWS } ] [ ONLY ]
 * [ ... ]
 */
public class SnowflakeDialect extends AbstractDialect {
    public SnowflakeDialect() {
        super();
        setLimitHandler(new OffsetFetchFirstOnlyLimitHandler());
    }


    @Override
    public boolean isSupportsLimitOffset() {
        return true;
    }
}
