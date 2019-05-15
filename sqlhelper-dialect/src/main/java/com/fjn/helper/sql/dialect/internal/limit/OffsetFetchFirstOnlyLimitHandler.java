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

package com.fjn.helper.sql.dialect.internal.limit;

import com.fjn.helper.sql.dialect.RowSelection;

import java.util.Locale;

public class OffsetFetchFirstOnlyLimitHandler extends AbstractLimitHandler {
    @Override
    public String processSql(String sql, RowSelection rowSelection) {
        return getLimitString(sql, rowSelection.getOffset(), getMaxOrLimit(rowSelection));
    }

    @Override
    protected String getLimitString(String sql, int offset, int limit) {
        // https://fmhelp.filemaker.com/docs/16/en/fm16_sql_reference.pdf
        // https://documentation.progress.com/output/ua/OpenEdge_latest/#page/dmsrf%2Foffset-and-fetch-clauses.html%23wwID0E6CPQ
        boolean hasOffset = offset>0;
        sql = sql.trim();
        String forUpdateClause = "";
        boolean isForUpdate = false;
        String sqlLowercase = sql.toLowerCase(Locale.ROOT);
        int forUpdateIndex = sqlLowercase.lastIndexOf("for update");
        if (forUpdateIndex > -1) {
            forUpdateClause = sql.substring(forUpdateIndex);
            sql = sql.substring(0, forUpdateIndex - 1);
            isForUpdate = true;
        }

        int withClauseIndex = sqlLowercase.lastIndexOf("with ",sqlLowercase.length()- (forUpdateClause.length()+7));
        boolean hasWithClause = false;
        String withClause = null;
        if(withClauseIndex>0){
            sql = sql.substring(0, withClauseIndex-1);
            hasWithClause = true;
            withClause = sqlLowercase.substring(withClauseIndex);
        }

        StringBuilder sql2 = new StringBuilder(sql.length() + 100);
        sql2.append(sql);

        if(getDialect().isSupportsVariableLimit()) {
            if (hasOffset) {
                sql2.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
            } else {
                sql2.append(" FETCH FIRST ? ROWS ONLY");
            }
        }else{
            if (hasOffset) {
                sql2.append(" OFFSET "+offset+" ROWS FETCH NEXT "+limit+" ROWS ONLY");
            } else {
                sql2.append(" FETCH FIRST "+limit+" ROWS ONLY");
            }
        }
        if(hasWithClause){
            sql2.append(withClause);
        }
        else if(isForUpdate){
            sql2.append(forUpdateClause);
        }
        return sql2.toString();
    }
}
