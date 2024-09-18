package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.TopLimitHandler;

/**
 * http://docs.sqream.com/latest/manual/Content/SQL_Reference_Guide/19_2.4_Queries.htm?tocpath=SQream%20DB%20%20SQL%20Reference%20Guide%7CQueries%7C_____0#Queries_..274
 */
public class SQReamDialect extends AbstractDialect {
    public SQReamDialect() {
        super();
        setLimitHandler(new TopLimitHandler());
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return false;
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }
}
