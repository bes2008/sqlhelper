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

public class OracleXLimitHandler extends AbstractLimitHandler {
    @Override
    public String processSql(String sql,boolean isSubquery, boolean useLimitVariable, RowSelection selection) {
        boolean hasOffset = LimitHelper.hasFirstRow(selection);

        sql = sql.trim();
        String forUpdateClause = null;
        boolean isForUpdate = false;
        int forUpdateIndex = sql.toLowerCase(Locale.ROOT).lastIndexOf("for update");
        if (forUpdateIndex > -1) {
            forUpdateClause = sql.substring(forUpdateIndex);
            sql = sql.substring(0, forUpdateIndex - 1);
            isForUpdate = true;
        }

        StringBuilder pagingSelect = new StringBuilder(sql.length() + 100);
        if (hasOffset) {
            pagingSelect.append("select * from ( select sqlhelper_rowtable_.*, rownum rownum_ from ( ");
        } else {
            pagingSelect.append("select * from ( ");
        }
        pagingSelect.append(sql);
        pagingSelect.append(" ) sqlhelper_rowtable_");
        if(useLimitVariable && getDialect().isUseLimitInVariableMode(isSubquery)){
            if (hasOffset) {
                pagingSelect.append(" where rownum <= ?) where rownum_ > ?");
            } else {
                pagingSelect.append(" where rownum <= ?");
            }
        }else{
            int firstRow = (int)convertToFirstRowValue(LimitHelper.getFirstRow(selection));
            int lastRow = getMaxOrLimit(selection);
            if (hasOffset) {
                pagingSelect.append(" where rownum <= "+lastRow+") where rownum_ > "+firstRow);
            } else {
                pagingSelect.append(" where rownum <= "+lastRow);
            }
        }

        if (isForUpdate) {
            pagingSelect.append(" ");
            pagingSelect.append(forUpdateClause);
        }

        return pagingSelect.toString();
    }
}
