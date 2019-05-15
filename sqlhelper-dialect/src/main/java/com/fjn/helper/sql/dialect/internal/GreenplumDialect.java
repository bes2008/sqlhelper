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

package com.fjn.helper.sql.dialect.internal;

import com.fjn.helper.sql.dialect.RowSelection;
import com.fjn.helper.sql.dialect.internal.limit.AbstractLimitHandler;
import com.fjn.helper.sql.dialect.internal.limit.LimitHelper;

import java.util.Locale;

public class GreenplumDialect extends AbstractDialect {
    public GreenplumDialect(){
        super();
        setLimitHandler(new AbstractLimitHandler() {
            @Override
            public String processSql(String sql, RowSelection rowSelection) {
                return getLimitString(sql, LimitHelper.hasFirstRow(rowSelection));
            }

            @Override
            protected String getLimitString(String sql, boolean hasOffset) {
                // http://gpdb.docs.pivotal.io/5180/ref_guide/sql_commands/SELECT.html
                sql = sql.trim();
                String forUpdateClause = null;
                boolean isForUpdate = false;
                int forUpdateIndex = sql.toLowerCase(Locale.ROOT).lastIndexOf("for update");
                if (forUpdateIndex > -1) {
                    forUpdateClause = sql.substring(forUpdateIndex);
                    sql = sql.substring(0, forUpdateIndex - 1);
                    isForUpdate = true;
                }

                String forShareClause = null;
                boolean isForShare = false;
                if(!isForUpdate) {
                    int forShareIndex = sql.toLowerCase(Locale.ROOT).lastIndexOf("for share");
                    if (forShareIndex > -1) {
                        forShareClause = sql.substring(forShareIndex);
                        sql = sql.substring(0, forShareIndex - 1);
                        isForShare = true;
                    }
                }

                StringBuilder sql2 = new StringBuilder(sql.length() + 100);
                sql2.append(sql);
                if(hasOffset){
                    sql2.append(" LIMIT ? OFFSET ? ");
                }else{
                    sql2.append(" LIMIT ?  ");
                }

                if(isForUpdate){
                    sql2.append(forUpdateClause);
                }
                if(isForShare){
                    sql2.append(forShareClause);
                }
                return sql2.toString();
            }
        });
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
}
