package com.jn.sqlhelper.dialect.scriptfile;

import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlStatementBuilder;

/**
 * supporting Derby-specific delimiter changes.
 */
public class DerbySqlStatementBuilder extends PlainSqlStatementBuilder {
    @Override
    protected String extractAlternateOpenQuote(String token) {
        if (token.startsWith("$$")) {
            return "$$";
        }
        return null;
    }
}
