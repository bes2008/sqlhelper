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

package com.jn.sqlhelper.dialect.internal.limit;

import com.jn.sqlhelper.dialect.pagination.RowSelection;

import java.util.Locale;

/**
 * select * from TABLE where xxx
 * limit $offset, $limit
 * [for update]
 * every dialect use the limitHandler should set bindLimitParameterInReverseOrder = false
 */
public class LimitCommaLimitHandler extends AbstractLimitHandler {
    private boolean isSupportForUpdate;
    private boolean withBrace = false;

    private int offsetBased = 0;

    @Override
    public String processSql(String sql,boolean isSubquery, RowSelection rowSelection) {
        return getLimitString(sql,isSubquery, LimitHelper.getFirstRow(rowSelection), getMaxOrLimit(rowSelection));
    }



    @Override
    protected String getLimitString(String sql,boolean isSubquery, long offset, int limit) {
        boolean hasOffset = offset > 0;

        sql = sql.trim();
        String forUpdateClause = "";
        boolean isForUpdate = false;
        if (isSupportForUpdate()) {
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

        if (getDialect().isUseLimitInVariableMode(isSubquery)) {
            if (withBrace) {
                sql2.append(hasOffset ? " limit (?, ?)" : " limit ?");
            } else {
                sql2.append(hasOffset ? " limit ?, ?" : " limit ?");
            }
        } else {
            if (withBrace) {
                sql2.append(hasOffset ? (" limit (" + offset + ", " + limit + ")") : (" limit " + limit));
            } else {
                sql2.append(hasOffset ? (" limit " + offset + ", " + limit) : (" limit " + limit));
            }
        }
        if (isForUpdate) {
            sql2.append(" " + forUpdateClause);
        }
        return sql2.toString();
    }

    public boolean isSupportForUpdate() {
        return isSupportForUpdate;
    }

    public LimitCommaLimitHandler setSupportForUpdate(boolean supportForUpdate) {
        isSupportForUpdate = supportForUpdate;
        return this;
    }

    public boolean isWithBrace() {
        return withBrace;
    }

    public LimitCommaLimitHandler setWithBrace(boolean withBrace) {
        this.withBrace = withBrace;
        return this;
    }

    public int getOffsetBased() {
        return offsetBased;
    }

    public void setOffsetBased(int offsetBased) {
        this.offsetBased = offsetBased;
    }
}
