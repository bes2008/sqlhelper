/*
 * Copyright 2020 the original author or authors.
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

package com.jn.sqlhelper.jsqlparser.sqlparser;

import com.jn.langx.util.Preconditions;
import com.jn.sqlhelper.dialect.sqlparser.AbstractSqlStatementWrapper;
import net.sf.jsqlparser.statement.Statement;

public class JSqlParserStatementWrapper extends AbstractSqlStatementWrapper<Statement> {

    public JSqlParserStatementWrapper(Statement statement) {
        Preconditions.checkNotNull(statement);
        setStatement(statement);
    }

    @Override
    public String getSql() {
        if (!isChanged()) {
            return getOriginalSql();
        }
        return get().toString();
    }
}
