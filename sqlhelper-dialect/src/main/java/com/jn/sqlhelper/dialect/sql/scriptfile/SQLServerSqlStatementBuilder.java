package com.jn.sqlhelper.dialect.sql.scriptfile;

import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlDelimiter;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlStatementBuilder;

/**
 * supporting SQL Server-specific delimiter changes.
 */
public class SQLServerSqlStatementBuilder extends PlainSqlStatementBuilder {
    @Override
    protected PlainSqlDelimiter getDefaultDelimiter() {
        return new PlainSqlDelimiter("GO", true);
    }

    @Override
    protected String extractAlternateOpenQuote(String token) {
        if (token.startsWith("N'")) {
            return "N'";
        }
        return null;
    }

    @Override
    protected String computeAlternateCloseQuote(String openQuote) {
        return "'";
    }
}