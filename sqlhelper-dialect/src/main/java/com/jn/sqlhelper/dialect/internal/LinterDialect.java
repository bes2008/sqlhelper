package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.LimitCommaLimitHandler;

/**
 * https://linter.ru/ru/documentations/6.0.17.86/manual/spravochnik-po-sql-ogranichenie-vyborki/
 * limit offset, limit
 */
public class LinterDialect extends AbstractDialect {

    public LinterDialect() {
        super();
        setLimitHandler(new LimitCommaLimitHandler());
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