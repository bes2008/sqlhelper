package com.jn.sqlhelper.dialect.scriptfile;

import com.jn.langx.util.Strings;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlDelimiter;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlStatementBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * supporting Oracle-specific PL/SQL constructs.
 */
public class OracleSqlStatementBuilder extends PlainSqlStatementBuilder {
    /**
     * Regex for keywords that can appear before a string literal without being separated by a space.
     */
    private static final Pattern KEYWORDS_BEFORE_STRING_LITERAL_REGEX = Pattern.compile("^(N|IF|ELSIF|SELECT|IMMEDIATE|RETURN|IS)('.*)");

    /**
     * Regex for keywords that can appear after a string literal without being separated by a space.
     */
    private static final Pattern KEYWORDS_AFTER_STRING_LITERAL_REGEX = Pattern.compile("(.*')(USING|THEN|FROM)");

    /**
     * Delimiter of PL/SQL blocks and statements.
     */
    private static final PlainSqlDelimiter PLSQL_DELIMITER = new PlainSqlDelimiter("/", true);
    /**
     * Holds the beginning of the statement.
     */
    private String statementStart = "";

    @Override
    protected PlainSqlDelimiter changeDelimiterIfNecessary(String line, PlainSqlDelimiter delimiter) {
        if (line.matches("DECLARE|DECLARE\\s.*") || line.matches("BEGIN|BEGIN\\s.*")) {
            return PLSQL_DELIMITER;
        }

        if (Strings.countOccurrencesOf(statementStart, " ") < 8) {
            statementStart += line;
            statementStart += " ";
            statementStart = statementStart.replaceAll("\\s+", " ");
        }

        if (statementStart.matches("CREATE( OR REPLACE)? (FUNCTION|PROCEDURE|PACKAGE|TYPE|TRIGGER).*")
                || statementStart.matches("CREATE( OR REPLACE)?( AND (RESOLVE|COMPILE))?( NOFORCE)? JAVA (SOURCE|RESOURCE|CLASS).*")){
            return PLSQL_DELIMITER;
        }

        return delimiter;
    }

    @Override
    protected String cleanToken(String token) {
        Matcher beforeMatcher = KEYWORDS_BEFORE_STRING_LITERAL_REGEX.matcher(token);
        if (beforeMatcher.find()) {
            token = beforeMatcher.group(2);
        }

        Matcher afterMatcher = KEYWORDS_AFTER_STRING_LITERAL_REGEX.matcher(token);
        if (afterMatcher.find()) {
            token = afterMatcher.group(1);
        }

        return token;
    }

    @Override
    protected String simplifyLine(String line) {
        String simplifiedQQuotes = Strings.replace(Strings.replace(line, "q'(", "q'["), ")'", "]'");
        return super.simplifyLine(simplifiedQQuotes);
    }

    @Override
    protected String extractAlternateOpenQuote(String token) {
        if (token.startsWith("Q'") && (token.length() >= 3)) {
            return token.substring(0, 3);
        }
        return null;
    }

    @Override
    protected String computeAlternateCloseQuote(String openQuote) {
        char specialChar = openQuote.charAt(2);
        switch (specialChar) {
            case '[':
                return "]'";
            case '(':
                return ")'";
            case '{':
                return "}'";
            case '<':
                return ">'";
            default:
                return specialChar + "'";
        }
    }
}



