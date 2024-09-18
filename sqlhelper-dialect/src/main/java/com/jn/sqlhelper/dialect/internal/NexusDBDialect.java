package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.TopLimitHandler;

/**
 * https://www.nexusdb.com/support/index.php?q=selectstatement.htm
 */
public class NexusDBDialect extends AbstractDialect {
    public NexusDBDialect() {
        super();
        setLimitHandler(new TopLimitHandler());
    }

    @Override
    public boolean isBindLimitParametersFirst() {
        return true;
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
