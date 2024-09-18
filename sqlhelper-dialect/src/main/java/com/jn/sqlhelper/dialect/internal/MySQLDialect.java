package com.jn.sqlhelper.dialect.internal;

import com.jn.langx.util.Strings;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlDelimiter;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlScriptParser;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlStatementBuilder;
import com.jn.sqlhelper.dialect.likeescaper.BackslashStyleEscaper;
import com.jn.sqlhelper.dialect.internal.limit.LimitCommaLimitHandler;
import com.jn.sqlhelper.dialect.internal.urlparser.MySqlUrlParser;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class MySQLDialect extends AbstractDialect {

    public MySQLDialect() {
        super();
        setUrlParser(new MySqlUrlParser());
        setLimitHandler(new LimitCommaLimitHandler());
        setLikeEscaper(BackslashStyleEscaper.INSTANCE);
        setPlainSqlScriptParser(new MySQLScriptParser());
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
    public boolean isSupportsVariableLimitInSubquery() {
        return false;
    }

    @Override
    public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
        return col;
    }

    @Override
    public char getBeforeQuote() {
        return '`';
    }

    @Override
    public char getAfterQuote() {
        return '`';
    }

    @Override
    public boolean isSupportsBatchUpdates() {
        return true;
    }

    @Override
    public boolean isSupportsBatchSql() {
        return true;
    }

    private static class MySQLScriptParser extends PlainSqlScriptParser {
        @Override
        protected PlainSqlStatementBuilder newSqlStatementBuilder() {
            return new MySQLSqlStatementBuilder();
        }
    }


    /**
     * supporting MySQL-specific delimiter changes.
     */
    private static class MySQLSqlStatementBuilder extends PlainSqlStatementBuilder {
        /**
         * The keyword that indicates a change in delimiter.
         */
        private static final String DELIMITER_KEYWORD = "DELIMITER";
        private final String[] charSets = {
                "ARMSCII8", "ASCII", "BIG5", "BINARY", "CP1250", "CP1251", "CP1256", "CP1257", "CP850", "CP852", "CP866", "CP932",
                "DEC8", "EUCJPMS", "EUCKR", "GB2312", "GBK", "GEOSTD8", "GREEK", "HEBREW", "HP8", "KEYBCS2", "KOI8R", "KOI8U", "LATIN1",
                "LATIN2", "LATIN5", "LATIN7", "MACCE", "MACROMAN", "SJIS", "SWE7", "TIS620", "UCS2", "UJIS", "UTF8"
        };

        boolean isInMultiLineCommentDirective = false;

        @Override
        public PlainSqlDelimiter extractNewDelimiterFromLine(String line) {
            if (line.toUpperCase().startsWith(DELIMITER_KEYWORD)) {
                return new PlainSqlDelimiter(line.substring(DELIMITER_KEYWORD.length()).trim(), false);
            }

            return null;
        }

        @Override
        protected PlainSqlDelimiter changeDelimiterIfNecessary(String line, PlainSqlDelimiter delimiter) {
            if (line.toUpperCase().startsWith(DELIMITER_KEYWORD)) {
                return new PlainSqlDelimiter(line.substring(DELIMITER_KEYWORD.length()).trim(), false);
            }

            return delimiter;
        }

        @Override
        public boolean isCommentDirective(String line) {
            // single-line comment directive
            if (line.matches("^" + Pattern.quote("/*!") + "\\d{5} .*" + Pattern.quote("*/") + "\\s*;?")) {
                return true;
            }
            // last line of multi-line comment directive
            if (isInMultiLineCommentDirective && line.matches(".*" + Pattern.quote("*/") + "\\s*;?")) {
                isInMultiLineCommentDirective = false;
                return true;
            }
            // start of multi-line comment directive
            if (line.matches("^" + Pattern.quote("/*!") + "\\d{5} .*")) {
                isInMultiLineCommentDirective = true;
                return true;
            }
            return isInMultiLineCommentDirective;
        }

        @Override
        protected boolean isSingleLineComment(String line) {
            return line.startsWith("--") || line.startsWith("#");
        }

        @Override
        protected String removeEscapedQuotes(String token) {
            String noEscapedBackslashes = Strings.replace(token, "\\\\", "");
            String noBackslashEscapes = Strings.replace(Strings.replace(noEscapedBackslashes, "\\'", ""), "\\\"", "");
            return Strings.replace(noBackslashEscapes, "''", "").replace("'", " ' ");
        }

        @Override
        protected String cleanToken(String token) {
            if (token.startsWith("_")) {
                for (String charSet : charSets) {
                    String cast = "_" + charSet;
                    if (token.startsWith(cast)) {
                        return token.substring(cast.length());
                    }
                }
            }
            // If no matches are found for charset casting then return token
            return token;
        }

        @Override
        protected String extractAlternateOpenQuote(String token) {
            if (token.startsWith("\"")) {
                return "\"";
            }
            // to be a valid bitfield or hex literal the token must be at leas three characters in length
            // i.e. b'' otherwise token may be string literal ending in [space]b'
            if (token.startsWith("B'") && token.length() > 2) {
                return "B'";
            }
            if (token.startsWith("X'") && token.length() > 2) {
                return "X'";
            }
            return null;
        }

        @Override
        protected String computeAlternateCloseQuote(String openQuote) {
            if ("B'".equals(openQuote) || "X'".equals(openQuote)) {
                return "'";
            }
            return openQuote;
        }
    }


}
