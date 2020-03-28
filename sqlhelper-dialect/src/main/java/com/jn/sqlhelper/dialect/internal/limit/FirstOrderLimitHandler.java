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

public class FirstOrderLimitHandler extends AbstractLimitHandler {
    @Override
    public String processSql(String sql, RowSelection rowSelection) {
        return getLimitString(sql, LimitHelper.getFirstRow(rowSelection), getMaxOrLimit(rowSelection));
    }

    @Override
    protected String getLimitString(String sql, long offset, int limit) {
        boolean hasOffset = offset > 0;
        sql = sql.trim();

        boolean hasOrderByClause = false;
        String orderByClause = null;
        String sqlLowercase = sql.toLowerCase(Locale.ROOT);
        int orderByIndex = sqlLowercase.lastIndexOf("order by");
        if (orderByIndex > -1) {
            orderByClause = sql.substring(orderByIndex);
            sql = sql.substring(0, orderByIndex - 1);
            hasOrderByClause = true;
        }

        StringBuilder sql2 = new StringBuilder(sql.length() + 100);
        sql2.append(sql);

        // you can look FIRST as limit
        if (getDialect().isUseLimitInVariableMode()) {
            if (hasOffset) {
                sql2.append(" FIRST ? TO ? ");
            } else {
                sql2.append(" FIRST ? ");
            }
        } else {
            if (hasOffset) {
                sql2.append(" FIRST " + offset + " TO " + limit + " ");
            } else {
                sql2.append(" FIRST " + limit + " ");
            }
        }
        if (hasOrderByClause) {
            sql2.append(orderByClause);
        }
        return sql2.toString();
    }
}
