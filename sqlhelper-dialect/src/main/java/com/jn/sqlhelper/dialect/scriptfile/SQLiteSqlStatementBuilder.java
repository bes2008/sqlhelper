package com.jn.sqlhelper.dialect.scriptfile;

import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlDelimiter;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlStatementBuilder;

/**
 * supporting SQLite-specific delimiter changes.
 */
public class SQLiteSqlStatementBuilder extends PlainSqlStatementBuilder {
    /**
     * Are we inside a BEGIN block.
     */
    private boolean insideBeginEndBlock;

    @Override
    protected PlainSqlDelimiter changeDelimiterIfNecessary(String line, PlainSqlDelimiter delimiter) {
        if (line.contains("BEGIN")) {
            insideBeginEndBlock = true;
        }

        if (line.endsWith("END;")) {
            insideBeginEndBlock = false;
        }

        if (insideBeginEndBlock) {
            return null;
        }
        return getDefaultDelimiter();
    }
}

