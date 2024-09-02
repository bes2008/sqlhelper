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

import com.jn.sqlhelper.dialect.internal.limit.AbstractLimitHandler;
import com.jn.sqlhelper.dialect.internal.limit.LimitHelper;
import com.jn.sqlhelper.dialect.internal.limit.LimitOnlyLimitHandler;
import com.jn.sqlhelper.dialect.pagination.RowSelection;

/**
 * https://www.alibabacloud.com/help/doc-detail/73777.htm?spm=a2c63.p38356.b99.86.72a82f4dUDIiYI
 */
public class MaxComputeDialect extends AbstractDialect {
    public MaxComputeDialect() {
        super();
        setLimitHandler(new LimitOnlyLimitHandler());
    }

    private static class MaxComputeLimitHandler extends AbstractLimitHandler {
        @Override
        public String processSql(String sql,boolean isSubquery, boolean useLimitVariable, RowSelection rowSelection) {
            return getLimitString(sql,isSubquery, useLimitVariable, LimitHelper.getFirstRow(rowSelection), getMaxOrLimit(rowSelection));
        }

        @Override
        protected String getLimitString(String sql,boolean isSubquery, boolean useLimitVariable, long offset, int limit) {
            if (offset == 0) {
                if (useLimitVariable && getDialect().isUseLimitInVariableMode(isSubquery)) {
                    return sql + " limit ?";
                } else {
                    return sql + " limit " + limit;
                }
            } else {
                StringBuilder sqlBuilder = new StringBuilder(sql.length() + 256);
                sqlBuilder.append("select * from ( select row_number() over() as sqlhelper_ROW_ID, * from (")
                        .append(sql)
                        .append(" ) ) sqlhelper_tmp  where sqlhelper_ROW_ID ");
                if (useLimitVariable && getDialect().isUseLimitInVariableMode(isSubquery)) {
                    sqlBuilder.append(" between ? and ? ");
                } else {
                    int firstRow = (int)convertToFirstRowValue(offset);
                    int lastRow = getDialect().isUseMaxForLimit() ? (limit + (int)firstRow) : limit;
                    sqlBuilder.append(" between "+firstRow+" and "+ lastRow);
                }
                return sqlBuilder.toString();
            }
        }
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return true;
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }
}
