package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.LimitCommaLimitHandler;

/**
 * https://www.kinetica.com/docs/concepts/sql.html#query
 *
 * @author https://github.com/f1194361820
 */
public class KineticaDialect extends AbstractDialect {
    public KineticaDialect() {
        super();
        setLimitHandler(new LimitCommaLimitHandler());
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return true;
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }
}
