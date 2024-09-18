package com.jn.sqlhelper.dialect.internal;

import com.jn.langx.annotation.Name;
import com.jn.sqlhelper.dialect.internal.limit.FirstLimitHandler;

@Name("esgyn")
public class EsgynDBDialect extends AbstractDialect {
    public EsgynDBDialect() {
        super();
        setLimitHandler(new FirstLimitHandler());
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return false;
    }
}
