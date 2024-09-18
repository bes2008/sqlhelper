
package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.LimitOffsetLimitHandler;
import com.jn.sqlhelper.dialect.urlparser.OscarUrlParser;

public class OscarDialect extends AbstractDialect {
    public OscarDialect() {
        setUrlParser(new OscarUrlParser());
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

    @Override
    public boolean isSupportsLimitOffset() {
        return true;
    }

}
