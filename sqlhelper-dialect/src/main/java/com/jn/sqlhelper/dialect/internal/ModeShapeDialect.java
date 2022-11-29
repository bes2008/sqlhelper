package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.LimitOffsetLimitHandler;

/**
 * https://docs.jboss.org/author/display/MODE50/JCR-SQL2.html#103547153_JCR-SQL2-Limitandoffset
 *
 *
 * LIMIT $count OFFSET $offset;
 */
public class ModeShapeDialect extends AbstractDialect {
    public ModeShapeDialect() {
        setLimitHandler(new LimitOffsetLimitHandler());
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isBindLimitParametersInReverseOrder() {
        return true;
    }

}
