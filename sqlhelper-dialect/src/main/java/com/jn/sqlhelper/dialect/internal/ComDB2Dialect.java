package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.LimitOffsetLimitHandler;
import com.jn.sqlhelper.dialect.UrlParser;

/**
 * https://bloomberg.github.io/comdb2/sql.html#select-statement
 * <p>
 * supports 2 styles limit syntax:
 * 1) limit $limit offset $offset
 * 2) limit $offset, $limit
 * <p>
 * We use 1)
 *
 * https://bloomberg.github.io/comdb2/sql.html
 */
public class ComDB2Dialect extends AbstractDialect {
    @Override
    protected void setUrlParser(UrlParser urlParser) {
        super.setUrlParser(urlParser);
        setLimitHandler(new LimitOffsetLimitHandler());
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
