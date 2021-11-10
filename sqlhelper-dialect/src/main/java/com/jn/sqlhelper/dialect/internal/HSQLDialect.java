
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

import com.jn.langx.annotation.Name;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlDelimiter;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlScriptParser;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlStatementBuilder;
import com.jn.sqlhelper.dialect.internal.limit.OffsetFetchFirstOnlyLimitHandler;

/**
 * HyperSQL
 * http://hsqldb.org/doc/2.0/guide/dataaccess-chapt.html#dac_sql_select_statement
 */
@Name("hsql")
public class HSQLDialect extends AbstractDialect {

    public HSQLDialect() {
        super();
        setLimitHandler(new OffsetFetchFirstOnlyLimitHandler().setSupportUsingIndexClauseInSelectEnd(true));
        setPlainSqlScriptParser(new HSQLSqlScriptParser());
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isBindLimitParametersFirst() {
        return false;
    }

    private static class HSQLSqlScriptParser extends PlainSqlScriptParser{
        @Override
        protected PlainSqlStatementBuilder newSqlStatementBuilder() {
            return new HsqlSqlStatementBuilder();
        }
    }

    /**
     * supporting Hsql-specific delimiter changes.
     */
    private static class HsqlSqlStatementBuilder extends PlainSqlStatementBuilder {
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


}
