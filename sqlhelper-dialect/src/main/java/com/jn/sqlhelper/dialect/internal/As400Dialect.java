package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.annotation.Driver;
import com.jn.sqlhelper.dialect.internal.limit.OffsetFetchFirstOnlyLimitHandler;
import com.jn.sqlhelper.dialect.likeescaper.BackslashStyleEscaper;

/**
 * https://www.ibm.com/docs/en/i/7.2?topic=ssw_ibm_i_72/rzahh/javadoc/com/ibm/as400/access/AS400JDBCDriver.html
 *
 *  URL syntax: jdbc:as400://system-name/default-schema;properties
 *
 *  SQL Ref: https://www.ibm.com/docs/en/i/7.2?topic=reference-sql
 *
 *  offset ? rows fetch next ? rows only
 */
@Driver("com.ibm.as400.access.AS400JDBCDriver")
public class As400Dialect extends AbstractDialect{
    public As400Dialect(){
        setLimitHandler(new OffsetFetchFirstOnlyLimitHandler());
        setLikeEscaper(BackslashStyleEscaper.NON_DEFAULT_INSTANCE);
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return true;
    }

}
