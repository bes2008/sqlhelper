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

package com.jn.sqlhelper.common.formatter;

import com.jn.langx.Formatter;
import com.jn.langx.util.Strings;
import com.jn.langx.util.io.LineDelimiter;

import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Performs formatting of DDL SQL statements.
 */
public class SqlDdlFormatter implements Formatter<String, String> {

    private static final String INITIAL_LINE = LineDelimiter.DEFAULT.getValue() + "    ";
    private static final String OTHER_LINES = LineDelimiter.DEFAULT.getValue() + "       ";
    /**
     * Singleton access
     */
    public static final SqlDdlFormatter INSTANCE = new SqlDdlFormatter();

    @Override
    public String format(String sql, Object... args) {
        return format(sql);
    }

    protected String format(String sql) {
        if (Strings.isEmpty(sql)) {
            return sql;
        }

        if (sql.toLowerCase(Locale.ROOT).startsWith("create table")) {
            return formatCreateTable(sql);
        } else if (sql.toLowerCase(Locale.ROOT).startsWith("create")) {
            return sql;
        } else if (sql.toLowerCase(Locale.ROOT).startsWith("alter table")) {
            return formatAlterTable(sql);
        } else if (sql.toLowerCase(Locale.ROOT).startsWith("comment on")) {
            return formatCommentOn(sql);
        } else {
            return INITIAL_LINE + sql;
        }
    }

    private String formatCommentOn(String sql) {
        final StringBuilder result = new StringBuilder(60).append(INITIAL_LINE);
        final StringTokenizer tokens = new StringTokenizer(sql, " '[]\"", true);

        boolean quoted = false;
        while (tokens.hasMoreTokens()) {
            final String token = tokens.nextToken();
            result.append(token);
            if (isQuote(token)) {
                quoted = !quoted;
            } else if (!quoted) {
                if ("is".equals(token)) {
                    result.append(OTHER_LINES);
                }
            }
        }

        return result.toString();
    }

    private String formatAlterTable(String sql) {
        final StringBuilder result = new StringBuilder(60).append(INITIAL_LINE);
        final StringTokenizer tokens = new StringTokenizer(sql, " (,)'[]\"", true);

        boolean quoted = false;
        while (tokens.hasMoreTokens()) {
            final String token = tokens.nextToken();
            if (isQuote(token)) {
                quoted = !quoted;
            } else if (!quoted) {
                if (isBreak(token)) {
                    result.append(OTHER_LINES);
                }
            }
            result.append(token);
        }

        return result.toString();
    }

    private String formatCreateTable(String sql) {
        final StringBuilder result = new StringBuilder(60).append(INITIAL_LINE);
        final StringTokenizer tokens = new StringTokenizer(sql, "(,)'[]\"", true);

        int depth = 0;
        boolean quoted = false;
        while (tokens.hasMoreTokens()) {
            final String token = tokens.nextToken();
            if (isQuote(token)) {
                quoted = !quoted;
                result.append(token);
            } else if (quoted) {
                result.append(token);
            } else {
                if (")".equals(token)) {
                    depth--;
                    if (depth == 0) {
                        result.append(INITIAL_LINE);
                    }
                }
                result.append(token);
                if (",".equals(token) && depth == 1) {
                    result.append(OTHER_LINES);
                }
                if ("(".equals(token)) {
                    depth++;
                    if (depth == 1) {
                        result.append(OTHER_LINES);
                    }
                }
            }
        }

        return result.toString();
    }

    private static boolean isBreak(String token) {
        return "drop".equals(token) ||
                "add".equals(token) ||
                "references".equals(token) ||
                "foreign".equals(token) ||
                "on".equals(token);
    }

    private static boolean isQuote(String tok) {
        return "\"".equals(tok) ||
                "`".equals(tok) ||
                "]".equals(tok) ||
                "[".equals(tok) ||
                "'".equals(tok);
    }

}