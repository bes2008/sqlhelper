package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.LimitCommaLimitHandler;

/**
 * https://www.cubrid.org/manual/en/10.2/sql/query/select.html
 * https://www.cubrid.org/manual/en/10.2/sql/function/condition_op.html#like
 *
 * @author https://github.com/f1194361820
 */
public class CubridDialect extends AbstractDialect {
    public CubridDialect() {
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

    @Override
    public char getBeforeQuote() {
        return '[';
    }

    @Override
    public char getAfterQuote() {
        return ']';
    }
}
