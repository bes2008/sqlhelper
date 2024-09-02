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
import com.jn.sqlhelper.dialect.internal.sqlscript.PostgreSQLSqlStatementBuilder;
import com.jn.sqlhelper.dialect.pagination.RowSelection;
import com.jn.sqlhelper.dialect.internal.limit.AbstractLimitHandler;
import com.jn.sqlhelper.dialect.internal.limit.LimitHelper;

public class VerticaDialect extends AbstractDialect {
    public VerticaDialect() {
        setLimitHandler(new AbstractLimitHandler() {
            @Override
            public String processSql(String sql, boolean isSubquery, boolean useLimitVariable, RowSelection selection) {
                if(useLimitVariable && getDialect().isUseLimitInVariableMode(isSubquery)) {
                    return sql + " limit ?";
                }else {
                    int lastRow = getMaxOrLimit(selection);
                    return sql + " limit " + lastRow;
                }
            }
        });
        setPlainSqlScriptParser(new VerticaSqlScriptParser());
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return false;
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    private static class VerticaSqlScriptParser extends PlainSqlScriptParser{
        @Override
        protected PlainSqlStatementBuilder newSqlStatementBuilder() {
            return new VerticaStatementBuilder();
        }
    }

    /**
     * supporting Vertica specific syntax.
     */
    private static class VerticaStatementBuilder extends PostgreSQLSqlStatementBuilder {

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


}
