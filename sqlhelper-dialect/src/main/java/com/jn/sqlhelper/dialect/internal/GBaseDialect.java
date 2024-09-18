package com.jn.sqlhelper.dialect.internal;

import com.jn.langx.annotation.Name;
import com.jn.sqlhelper.common.ddl.SQLSyntaxCompatTable;
import com.jn.sqlhelper.dialect.annotation.SyntaxCompat;
import com.jn.sqlhelper.dialect.internal.limit.LimitCommaLimitHandler;
import com.jn.sqlhelper.dialect.likeescaper.BackslashStyleEscaper;

/**
 * http://www.gbase.cn/download/&pageNo=4&pageSize=10.html
 * MySQL Syntax Compatible
 * 适应于 GBase 8a
 */
@Name("gbase")
@SyntaxCompat(SQLSyntaxCompatTable.MYSQL)
public class GBaseDialect extends AbstractDialect {
    public GBaseDialect() {
        super();
        setLimitHandler(new LimitCommaLimitHandler());
        setLikeEscaper(new BackslashStyleEscaper(true));
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
