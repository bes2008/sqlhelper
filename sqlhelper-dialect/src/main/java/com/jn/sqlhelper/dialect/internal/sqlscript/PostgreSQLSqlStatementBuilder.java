/**
 * Copyright 2010-2015 Axel Fontaine
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jn.sqlhelper.dialect.internal.sqlscript;

import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlDelimiter;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlStatementBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * supporting PostgreSQL specific syntax.
 */
public class PostgreSQLSqlStatementBuilder extends PlainSqlStatementBuilder {
    /**
     * Delimiter of COPY statements.
     */
    private static final PlainSqlDelimiter COPY_DELIMITER = new PlainSqlDelimiter("\\.", true);

    /**
     * Matches $$, $BODY$, $xyz123$, ...
     */
    /*private -> for testing*/
    static final String DOLLAR_QUOTE_REGEX = "(\\$[A-Za-z0-9_]*\\$).*";

    /**
     * Are we at the beginning of the statement.
     */
    private boolean firstLine = true;

    /**
     * Whether this statement is a COPY statement.
     */
    private boolean pgCopy;

    @Override
    protected String extractAlternateOpenQuote(String token) {
        Matcher matcher = Pattern.compile(DOLLAR_QUOTE_REGEX).matcher(token);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    @Override
    protected PlainSqlDelimiter changeDelimiterIfNecessary(String line, PlainSqlDelimiter delimiter) {
        if (firstLine) {
            firstLine = false;
            if (line.matches("COPY|COPY\\s.*")) {
                pgCopy = true;
                return COPY_DELIMITER;
            }
        }

        return pgCopy ? COPY_DELIMITER : delimiter;
    }

    @Override
    public boolean isPgCopy() {
        return pgCopy;
    }
}
