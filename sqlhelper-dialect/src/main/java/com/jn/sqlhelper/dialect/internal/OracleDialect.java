
/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the LGPL, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jn.sqlhelper.dialect.internal;

import com.jn.langx.util.Strings;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlDelimiter;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlScriptParser;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlStatementBuilder;
import com.jn.sqlhelper.dialect.pagination.RowSelection;
import com.jn.sqlhelper.dialect.SQLDialectException;
import com.jn.sqlhelper.dialect.annotation.Driver;
import com.jn.sqlhelper.dialect.likeescaper.BackslashStyleEscaper;
import com.jn.sqlhelper.dialect.internal.limit.AbstractLimitHandler;
import com.jn.sqlhelper.dialect.internal.limit.LimitHelper;
import com.jn.sqlhelper.dialect.internal.limit.OracleXLimitHandler;
import com.jn.sqlhelper.dialect.internal.urlparser.OracleUrlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Driver({"oracle.jdbc.OracleDriver","oracle.jdbc.driver.OracleDriver"})
public class OracleDialect extends AbstractDialect {
    private static final int PARAM_LIST_SIZE_LIMIT = 1000;

    public OracleDialect() {
        super();
        setDelegate(new Oracle9iDialect());
        setUrlParser(new OracleUrlParser());
        setLikeEscaper(BackslashStyleEscaper.NON_DEFAULT_INSTANCE);
        setPlainSqlScriptParser(new OracleSqlScriptParser());
    }

    public OracleDialect(java.sql.Driver driver) {
        setUrlParser(new OracleUrlParser());
        setLikeEscaper(BackslashStyleEscaper.NON_DEFAULT_INSTANCE);
        setPlainSqlScriptParser(new OracleSqlScriptParser());

        int majorVersion = driver.getMajorVersion();
        int minorVersion = driver.getMinorVersion();
        if (majorVersion < 9) {
            setDelegate(new Oracle8iDialect());
            return;
        }
        if (majorVersion == 9) {
            setDelegate(new Oracle9iDialect());
            return;
        }
        if (majorVersion == 10) {
            if (minorVersion < 3) {
                setDelegate(new Oracle10gDialect());
                return;
            }
            setDelegate(new Oracle11gDialect());
            return;
        }

        if (majorVersion >= 12) {
            setDelegate(new Oracle12cDialect());
            return;
        }
        setDelegate(new Oracle9Dialect());

    }

    class OracleBaseDialect extends AbstractDialect {
        OracleBaseDialect() {
            setLikeEscaper(BackslashStyleEscaper.NON_DEFAULT_INSTANCE);
            setUrlParser(new OracleUrlParser());
            setPlainSqlScriptParser(new OracleSqlScriptParser());
        }

        @Override
        public IdentifierCase identifierCase() {
            return IdentifierCase.UPPERCASE;
        }

        @Override
        public boolean isSupportsLimit() {
            return true;
        }

        @Override
        public boolean isBindLimitParametersInReverseOrder() {
            return true;
        }

        @Override
        public boolean isUseMaxForLimit() {
            return true;
        }

        @Override
        public int registerResultSetOutParameter(CallableStatement statement, int col)
                throws SQLException {
            statement.registerOutParameter(col, OracleTypesHelper.INSTANCE.getOracleCursorTypeSqlType());
            col++;
            return col;
        }

        @Override
        public boolean isSupportsBatchUpdates() {
            return true;
        }

        @Override
        public boolean isSupportsBatchSql() {
            return true;
        }
    }

    private static class Oracle8i9LimitHandler extends AbstractLimitHandler {
        @Override
        public String processSql(String sql,boolean isSubquery, boolean useLimitVariable, RowSelection selection) {
            boolean hasOffset = LimitHelper.hasFirstRow(selection);
            sql = sql.trim();
            boolean isForUpdate = false;
            if (sql.toLowerCase(Locale.ROOT).endsWith(" for update")) {
                sql = sql.substring(0, sql.length() - 11);
                isForUpdate = true;
            }

            StringBuilder pagingSelect = new StringBuilder(sql.length() + 100);
            if (hasOffset) {
                pagingSelect.append("select * from ( select sqlhelper_rowtable_.*, rownum rownum_ from ( ");
            } else {
                pagingSelect.append("select * from ( ");
            }
            pagingSelect.append(sql);

            if(useLimitVariable && this.getDialect().isUseLimitInVariableMode(isSubquery)){
                if (hasOffset) {
                    pagingSelect.append(" ) sqlhelper_rowtable_ ) where rownum_ <= ? and rownum_ > ?");
                } else {
                    pagingSelect.append(" ) sqlhelper_rowtable_ where rownum <= ?");
                }
            }else{
                int firstRow = (int)convertToFirstRowValue(LimitHelper.getFirstRow(selection));
                int lastRow = getMaxOrLimit(selection);
                if (hasOffset) {
                    pagingSelect.append(" ) sqlhelper_rowtable_ ) where rownum_ <= "+lastRow+" and rownum_ > " + firstRow +" ");
                } else {
                    pagingSelect.append(" ) sqlhelper_rowtable_ where rownum <= "+lastRow+" ");
                }
            }


            if (isForUpdate) {
                pagingSelect.append(" for update");
            }

            return pagingSelect.toString();
        }
    }

    private class Oracle8iDialect extends OracleBaseDialect {
        private Oracle8iDialect() {
            super();
            setLimitHandler(new Oracle8i9LimitHandler());
        }
    }

    private class Oracle9Dialect extends OracleBaseDialect {
        private Oracle9Dialect() {
            super();
            setLimitHandler(new Oracle8i9LimitHandler());
        }
    }

    private class Oracle9iDialect extends OracleBaseDialect {
        private Oracle9iDialect() {
            super();
            setLimitHandler(new OracleXLimitHandler());
        }
    }

    private class Oracle10gDialect extends Oracle9iDialect {
        private Oracle10gDialect() {
            super();
        }
    }

    private class Oracle11gDialect extends Oracle10gDialect {
        private Oracle11gDialect() {
            super();
        }
    }

    private class Oracle12cDialect extends Oracle11gDialect {
        private Oracle12cDialect() {
            super();
        }
    }

    private static class OracleTypesHelper {
        private static final Logger log = LoggerFactory.getLogger(OracleTypesHelper.class);


        public static final OracleTypesHelper INSTANCE = new OracleTypesHelper();

        private static final String ORACLE_TYPES_CLASS_NAME = "oracle.jdbc.OracleTypes";
        private static final String DEPRECATED_ORACLE_TYPES_CLASS_NAME = "oracle.jdbc.driver.OracleTypes";
        private final int oracleCursorTypeSqlType;

        private OracleTypesHelper() {
            int typeCode = -99;
            try {
                typeCode = extractOracleCursorTypeValue();
            } catch (Exception e) {
                log.warn("Unable to resolve Oracle CURSOR JDBC type code", e);
            }
            this.oracleCursorTypeSqlType = typeCode;
        }

        private int extractOracleCursorTypeValue() {
            try {
                return locateOracleTypesClass().getField("CURSOR").getInt(null);
            } catch (Exception se) {
                throw new SQLDialectException("Unable to access OracleTypes.CURSOR value", se);
            }
        }

        private Class locateOracleTypesClass() {
            try {
                return Class.forName("oracle.jdbc.OracleTypes");
            } catch (ClassNotFoundException e) {
                try {
                    return Class.forName("oracle.jdbc.driver.OracleTypes");
                } catch (ClassNotFoundException e2) {
                    throw new SQLDialectException(String.format("Unable to locate OracleTypes class using either known FQN [%s, %s]", new Object[]{"oracle.jdbc.OracleTypes", "oracle.jdbc.driver.OracleTypes"}), e);
                }
            }
        }


        public int getOracleCursorTypeSqlType() {
            return this.oracleCursorTypeSqlType;
        }
    }


    private static class OracleSqlScriptParser extends PlainSqlScriptParser{
        @Override
        protected PlainSqlStatementBuilder newSqlStatementBuilder() {
            return new OracleSqlStatementBuilder();
        }
    }


    /**
     * supporting Oracle-specific PL/SQL constructs.
     */
    private static class OracleSqlStatementBuilder extends PlainSqlStatementBuilder {
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





}
