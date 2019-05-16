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

/**
 * select * from
 * where xxxx
 * limit $limit offset $Offset
 *
 * every dialect use the limitHandler should set bindLimitParameterInReverseOrder = true
 *
 */
public class LimitOffsetLimitHandler extends AbstractLimitHandler{
    // OFFSET $offset ROWS
    private boolean hasOffsetRowsSuffix = false;
    private boolean isSupportForUpdate;

    @Override
    public String processSql(String sql, RowSelection rowSelection) {
        return getLimitString(sql, LimitHelper.getFirstRow(rowSelection), getMaxOrLimit(rowSelection));
    }

    @Override
    protected String getLimitString(String sql, int offset, int limit) {
        boolean hasOffset = offset>0;
        sql = sql.trim();
        String forUpdateClause = "";
        boolean isForUpdate = false;
        if(isSupportForUpdate()) {
            String sqlLowercase = sql.toLowerCase(Locale.ROOT);
            int forUpdateIndex = sqlLowercase.lastIndexOf("for update");
            if (forUpdateIndex > -1) {
                forUpdateClause = sql.substring(forUpdateIndex);
                sql = sql.substring(0, forUpdateIndex - 1);
                isForUpdate = true;
            }
        }

        StringBuilder sql2 = new StringBuilder(sql.length() + 100);
        sql2.append(sql);

        if(getDialect().isSupportsVariableLimit()) {
            if (hasOffset) {
                sql2.append(" limit ? offset ? " + (hasOffsetRowsSuffix ? "rows":""));
            } else {
                sql2.append(" limit ?");
            }
        }else{
            if (hasOffset) {
                sql2.append(" limit "+limit+" offset "+offset+" " + (hasOffsetRowsSuffix ? "rows":""));
            } else {
                sql2.append(" limit "+limit);
            }
        }
        if(isForUpdate){
            sql2.append(" " + forUpdateClause);
        }
        return sql2.toString();
    }


    public boolean isHasOffsetRowsSuffix() {
        return hasOffsetRowsSuffix;
    }

    public LimitOffsetLimitHandler setHasOffsetRowsSuffix(boolean hasOffsetRowsSuffix) {
        this.hasOffsetRowsSuffix = hasOffsetRowsSuffix;
        return this;
    }

    public boolean isSupportForUpdate() {
        return isSupportForUpdate;
    }

    public LimitOffsetLimitHandler setSupportForUpdate(boolean supportForUpdate) {
        isSupportForUpdate = supportForUpdate;
        return this;
    }
}
