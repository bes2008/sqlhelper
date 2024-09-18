package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.OffsetFetchFirstOnlyLimitHandler;

/**
 * LeanXcale Query Engine was forked from Apache Derby, so the Lx-DB SQL dialect is very similar to Apache Derby
 * <p>
 * https://s3-eu-west-1.amazonaws.com/doc-html/doc/latest/develop/index.html#_schemas_and_metadata
 */
public class LeanXcaleDialect extends AbstractDialect {
    public LeanXcaleDialect() {
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
