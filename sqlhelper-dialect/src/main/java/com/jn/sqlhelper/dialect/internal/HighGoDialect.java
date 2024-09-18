package com.jn.sqlhelper.dialect.internal;

import com.jn.langx.annotation.Name;
import com.jn.sqlhelper.common.ddl.SQLSyntaxCompatTable;
import com.jn.sqlhelper.dialect.annotation.SyntaxCompat;
import com.jn.sqlhelper.dialect.internal.limit.LimitOffsetLimitHandler;

/**
 * http://www.highgo.com/content.php?catid=75
 * based on PostgreSQL
 */
@Name("highgo")
@SyntaxCompat(SQLSyntaxCompatTable.POSTGRESQL)
public class HighGoDialect extends AbstractDialect {
    public HighGoDialect() {
        super();
        setLimitHandler(new LimitOffsetLimitHandler());
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
