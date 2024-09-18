package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.likeescaper.BackslashStyleEscaper;
import com.jn.sqlhelper.dialect.internal.limit.LimitOffsetLimitHandler;
import com.jn.sqlhelper.dialect.sql.scriptfile.H2SqlScriptParser;


public class H2Dialect extends AbstractDialect {

    public H2Dialect() {
        super();
        setLimitHandler(new LimitOffsetLimitHandler());
        setLikeEscaper(new BackslashStyleEscaper());
        setPlainSqlScriptParser(new H2SqlScriptParser());
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
