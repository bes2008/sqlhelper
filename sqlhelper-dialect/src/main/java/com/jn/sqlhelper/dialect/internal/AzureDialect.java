package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.likeescaper.BackslashStyleEscaper;
import com.jn.sqlhelper.dialect.internal.limit.OffsetFetchFirstOnlyLimitHandler;

/**
 * Microsoft Azure Cloud Database
 */
public class AzureDialect extends AbstractDialect {
    public AzureDialect() {
        super();
        setLimitHandler(new OffsetFetchFirstOnlyLimitHandler());
        setLikeEscaper(BackslashStyleEscaper.INSTANCE);
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return true;
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

}
