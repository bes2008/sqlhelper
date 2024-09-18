package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.LimitCommaLimitHandler;
import com.jn.sqlhelper.dialect.internal.limit.LimitOnlyLimitHandler;
import com.jn.sqlhelper.dialect.likeescaper.BackslashStyleEscaper;

public class ElasticsearchDialect extends AbstractDialect {
    public ElasticsearchDialect() {
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

    @Override
    public boolean isBindLimitParametersInReverseOrder() {
        return true;
    }
}
