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

import com.jn.sqlhelper.dialect.RowSelection;

import java.util.Locale;


public class TopLimitHandler extends AbstractLimitHandler {
    private static final String SELECT_LOWERCASE = "select";
    private static final String SELECT_DISTINCT_LOWERCASE = "select distinct";
    private static final String SELECT_ALL_LOWERCASE = "select all";
    private boolean useSkipTop = false;

    @Override
    public String processSql(String sql, RowSelection rowSelection) {
        return getLimitString(sql, LimitHelper.getFirstRow(rowSelection), getMaxOrLimit(rowSelection));
    }

    @Override
    protected String getLimitString(String sql, long offset, int limit) {
        /*
         *  reference: http://docs.openlinksw.com/virtuoso/topselectoption/
         *  Select Syntax:
         *
         *  query_term :  SELECT opt_top selection ....

            opt_top :  opt_all_distinct [ TOP INTNUM ]
                    |  opt_all_distinct [ TOP SKIPINTNUM, INTNUM ]
                    |  opt_all_distinct [ TOP (num_scalar_exp) ]
                    |  opt_all_distinct [ TOP (skip_num_scalar_exp, num_scalar_exp) ]
            opt_all_distinct : [ ALL | DISTINCT ]
         *
         */
        boolean hasOffset = offset > 0;
        sql = sql.trim();
        String sqlLowercase = sql.toLowerCase(Locale.ROOT);
        int selectIndex = sqlLowercase.indexOf(SELECT_LOWERCASE);
        int selectDistinctIndex = sqlLowercase.indexOf(SELECT_DISTINCT_LOWERCASE);
        int selectAllIndex = sqlLowercase.indexOf(SELECT_ALL_LOWERCASE);

        int insertionPoint = -1;
        if (selectDistinctIndex != -1) {
            insertionPoint = selectDistinctIndex + SELECT_DISTINCT_LOWERCASE.length();
        } else if (selectAllIndex != -1) {
            insertionPoint = selectAllIndex + SELECT_ALL_LOWERCASE.length();
        } else if (selectIndex != -1) {
            insertionPoint = selectIndex + SELECT_LOWERCASE.length();
        } else {
            return sql;
        }

        if (insertionPoint < 0) {
            return sql;
        }
        StringBuilder sql2 = new StringBuilder(sql.length() + 50).append(sql);
        if (getDialect().isUseLimitInVariableMode()) {
            if (getDialect().isSupportsLimitOffset() && hasOffset) {
                if (!isUseSkipTop()) {
                    sql2.insert(insertionPoint, " TOP ?, ? ");
                } else {
                    sql2.insert(insertionPoint, " SKIP ? TOP ? ");
                }
            } else {
                sql2.insert(insertionPoint, " TOP ? ");
            }
        } else {
            if (getDialect().isSupportsLimitOffset() && hasOffset) {
                if (!isUseSkipTop()) {
                    if (getDialect().isBindLimitParametersInReverseOrder()) {
                        sql2.insert(insertionPoint, " TOP " + limit + ", " + offset + " ");
                    } else {
                        sql2.insert(insertionPoint, " TOP " + offset + ", " + limit + " ");
                    }
                } else {
                    sql2.insert(insertionPoint, " SKIP " + offset + " TOP " + limit + " ");
                }

            } else {
                sql2.insert(insertionPoint, " TOP " + limit);
            }
        }
        return sql2.toString();
    }

    public boolean isUseSkipTop() {
        return useSkipTop;
    }

    public TopLimitHandler setUseSkipTop(boolean useSkipTop) {
        this.useSkipTop = useSkipTop;
        return this;
    }
}
