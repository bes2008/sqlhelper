package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.OffsetFetchFirstOnlyLimitHandler;

/**
 * // https://fmhelp.filemaker.com/docs/16/en/fm16_sql_reference.pdf
 */
public class FileMakerDialect extends AbstractDialect {
    public FileMakerDialect() {
        super();
        setLimitHandler(new OffsetFetchFirstOnlyLimitHandler());
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
