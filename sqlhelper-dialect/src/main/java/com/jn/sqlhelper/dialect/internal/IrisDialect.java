package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.TopLimitHandler;

/**
 * https://irisdocs.intersystems.com/irislatest/csp/docbook/DocBook.UI.Page.cls?KEY=RSQL_top
 */
public class IrisDialect extends AbstractDialect {
    public IrisDialect() {
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

    @Override
    public boolean isBindLimitParametersFirst() {
        return true;
    }
}
