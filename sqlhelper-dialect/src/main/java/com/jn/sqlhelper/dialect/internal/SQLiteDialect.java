
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

import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlDelimiter;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlScriptParser;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlStatementBuilder;
import com.jn.sqlhelper.dialect.likeescaper.BackslashStyleEscaper;
import com.jn.sqlhelper.dialect.internal.limit.LimitOffsetLimitHandler;

/**
 * https://www.sqlite.org/lang_select.html
 */
public class SQLiteDialect extends AbstractDialect {
    public SQLiteDialect() {
        super();
        setLimitHandler(new LimitOffsetLimitHandler());
        setLikeEscaper(BackslashStyleEscaper.NON_DEFAULT_INSTANCE);
        setPlainSqlScriptParser(new SQLiteSqlScriptParser());
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
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return true;
    }

    @Override
    public boolean isBindLimitParametersInReverseOrder() {
        return true;
    }


    private static class SQLiteSqlScriptParser extends PlainSqlScriptParser{
        @Override
        protected PlainSqlStatementBuilder newSqlStatementBuilder() {
            return new SQLiteSqlStatementBuilder();
        }
    }

    /**
     * supporting SQLite-specific delimiter changes.
     */
    private static class SQLiteSqlStatementBuilder extends PlainSqlStatementBuilder {
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




}
