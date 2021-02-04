package com.jn.sqlhelper.dialect.internal;

import com.jn.langx.annotation.Name;
import com.jn.sqlhelper.dialect.internal.limit.SkipLimitHandler;
@Name("sino")
public class SinoDBDialect extends AbstractDialect {
    public SinoDBDialect() {
        super();
        this.setLimitHandler(new SkipLimitHandler());
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
    public boolean isSupportsVariableLimit() {
        return false;
    }
}
