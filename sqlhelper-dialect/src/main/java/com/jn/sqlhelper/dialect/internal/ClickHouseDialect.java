package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.likeescaper.BackslashStyleEscaper;
import com.jn.sqlhelper.dialect.internal.limit.LimitCommaLimitHandler;

/**
 * https://clickhouse.yandex/docs/en/query_language/select/
 * https://clickhouse.yandex/docs/en/query_language/functions/string_search_functions/
 */
public class ClickHouseDialect extends AbstractDialect {
    public ClickHouseDialect() {
        super();
        setLimitHandler(new LimitCommaLimitHandler());
        setLikeEscaper(BackslashStyleEscaper.INSTANCE);
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return true;
    }
}
