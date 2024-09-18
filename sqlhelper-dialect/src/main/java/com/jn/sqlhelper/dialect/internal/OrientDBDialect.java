package com.jn.sqlhelper.dialect.internal;

import com.jn.langx.annotation.Name;
import com.jn.sqlhelper.dialect.internal.limit.SkipLimitHandler;

@Name("orient")
public class OrientDBDialect extends AbstractDialect {
    public OrientDBDialect() {
        super();
        setLimitHandler(new SkipLimitHandler("LIMIT"));
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