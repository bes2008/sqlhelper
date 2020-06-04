package com.jn.sqlhelper.dialect.internal;

import com.jn.langx.annotation.Name;
import com.jn.sqlhelper.common.ddl.SQLSyntaxCompatTable;
import com.jn.sqlhelper.dialect.annotation.SyntaxCompat;
import com.jn.sqlhelper.dialect.internal.limit.LimitCommaLimitHandler;

@Name("golden")
@SyntaxCompat(SQLSyntaxCompatTable.MYSQL)
public class GoldenDBDialect extends AbstractDialect {

    public GoldenDBDialect() {
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
        return '`';
    }

    @Override
    public char getAfterQuote() {
        return '`';
    }
}
