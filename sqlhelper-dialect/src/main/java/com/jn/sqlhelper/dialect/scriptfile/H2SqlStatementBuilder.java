package com.jn.sqlhelper.dialect.scriptfile;

import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlStatementBuilder;

/**
 * supporting H2-specific delimiter changes.
 */
public class H2SqlStatementBuilder extends PlainSqlStatementBuilder {
    @Override
    protected String extractAlternateOpenQuote(String token) {
        if (token.startsWith("$$")) {
            return "$$";
        }
        return null;
    }
}
