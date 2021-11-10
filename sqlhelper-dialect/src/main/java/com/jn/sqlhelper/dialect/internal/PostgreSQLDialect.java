
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

import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlScriptParser;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlStatementBuilder;
import com.jn.sqlhelper.dialect.internal.sqlscript.PostgreSQLSqlStatementBuilder;
import com.jn.sqlhelper.dialect.likeescaper.BackslashStyleEscaper;
import com.jn.sqlhelper.dialect.internal.limit.LimitOffsetLimitHandler;
import com.jn.sqlhelper.dialect.internal.urlparser.PostgreSQLUrlParser;

import java.sql.CallableStatement;
import java.sql.SQLException;

/**
 * https://www.postgresql.org/docs/current/queries-limit.html
 */
public class PostgreSQLDialect extends AbstractDialect {

    public PostgreSQLDialect() {
        super();
        setUrlParser(new PostgreSQLUrlParser());
        setLimitHandler(new LimitOffsetLimitHandler());
        setLikeEscaper(BackslashStyleEscaper.NON_DEFAULT_INSTANCE);
        setPlainSqlScriptParser(new PostgreSQLScriptParser());
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

    @Override
    public int registerResultSetOutParameter(CallableStatement statement, int col)
            throws SQLException {
        statement.registerOutParameter(col++, 1111);
        return col;
    }

    private static class PostgreSQLScriptParser extends PlainSqlScriptParser{
        @Override
        protected PlainSqlStatementBuilder newSqlStatementBuilder() {
            return new PostgreSQLSqlStatementBuilder();
        }
    }

}
