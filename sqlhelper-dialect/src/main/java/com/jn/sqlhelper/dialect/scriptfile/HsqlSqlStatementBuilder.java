package com.jn.sqlhelper.dialect.scriptfile;

import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlDelimiter;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlStatementBuilder;

/**
 * supporting Hsql-specific delimiter changes.
 */
public class HsqlSqlStatementBuilder extends PlainSqlStatementBuilder {
    /**
     * Are we inside a BEGIN ATOMIC block.
     */
    private boolean insideAtomicBlock;

    @Override
    protected PlainSqlDelimiter changeDelimiterIfNecessary(String line, PlainSqlDelimiter delimiter) {
        if (line.contains("BEGIN ATOMIC")) {
            insideAtomicBlock = true;
        }

        if (line.endsWith("END;")) {
            insideAtomicBlock = false;
        }

        if (insideAtomicBlock) {
            return null;
        }
        return getDefaultDelimiter();
    }
}
