package com.jn.sqlhelper.dialect.sql.scriptfile;

import com.jn.langx.util.Strings;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlDelimiter;

/**
 * supporting Vertica specific syntax.
 */
public class VerticaStatementBuilder extends PostgreSQLSqlStatementBuilder {

    /**
     * Are we currently inside a BEGIN END; block?
     */
    private boolean insideBeginEndBlock;

    /**
     * Holds the beginning of the statement.
     */
    private String statementStart = "";

    @Override
    protected PlainSqlDelimiter changeDelimiterIfNecessary(String line, PlainSqlDelimiter delimiter) {
        if (Strings.countOccurrencesOf(statementStart, " ") < 4) {
            statementStart += line;
            statementStart += " ";
        }

        if (statementStart.startsWith("CREATE FUNCTION")) {
            if (line.startsWith("BEGIN") || line.endsWith("BEGIN")) {
                insideBeginEndBlock = true;
            }

            if (line.endsWith("END;")) {
                insideBeginEndBlock = false;
            }
        }

        if (insideBeginEndBlock) {
            return null;
        }
        return getDefaultDelimiter();
    }

}