package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlScriptParser;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlStatementBuilder;
import com.jn.sqlhelper.dialect.likeescaper.BackslashStyleEscaper;
import com.jn.sqlhelper.dialect.internal.limit.LimitOffsetLimitHandler;


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

    private static class H2SqlScriptParser extends PlainSqlScriptParser{
        @Override
        protected PlainSqlStatementBuilder newSqlStatementBuilder() {
            return new PlainSqlStatementBuilder();
        }
    }

    /**
     * supporting H2-specific delimiter changes.
     */
    private static class H2SqlStatementBuilder extends PlainSqlStatementBuilder {
        @Override
        protected String extractAlternateOpenQuote(String token) {
            if (token.startsWith("$$")) {
                return "$$";
            }
            return null;
        }
    }



}
