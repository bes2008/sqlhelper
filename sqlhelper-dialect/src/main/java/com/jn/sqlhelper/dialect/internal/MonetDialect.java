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

import com.jn.langx.util.regexp.Regexp;
import com.jn.langx.util.regexp.Regexps;
import com.jn.sqlhelper.dialect.internal.limit.LimitHelper;
import com.jn.sqlhelper.dialect.pagination.RowSelection;
import com.jn.sqlhelper.dialect.internal.limit.AbstractLimitHandler;

public class MonetDialect extends AbstractDialect {
    private static final Regexp SAMPLE_SQL_PATTERN = Regexps.createRegexp(".* sample\\w+(\\d+|\\?)$");

    public MonetDialect() {
        super();
        setLimitHandler(new AbstractLimitHandler() {

            /**
             * https://www.monetdb.org/Documentation/Manuals/SQLreference/SQLSyntaxOverview#SELECT
             */
            @Override
            public String processSql(String sql, boolean isSubquery, boolean useLimitVariable, RowSelection selection) {
                sql = sql.trim();
                while (sql.endsWith(";")) {
                    sql = sql.substring(0, sql.length() - 1);
                }
                String lowercaseSql = sql.toLowerCase();
                boolean hasSampleClause = false;
                String sampleSql = null;

                if (SAMPLE_SQL_PATTERN.matcher(lowercaseSql).matches()) {
                    hasSampleClause = true;
                    int lastIndex = lowercaseSql.lastIndexOf("sample");
                    sampleSql = " " + sql.substring(lastIndex);
                    sql = sql.substring(0, lastIndex);
                }

                boolean hasOffset = selection.hasOffset();
                if(useLimitVariable && isUseLimitInVariableMode(isSubquery)) {
                    sql = sql + (hasOffset ? " limit ? offset ? " : " limit ? ") ;
                }else{
                    int lastRow = getMaxOrLimit(selection);
                    sql = sql + " limit " + lastRow;
                    if(hasOffset){
                        int firstRow = (int)convertToFirstRowValue(LimitHelper.getFirstRow(selection));
                        sql = sql + " offset "+ firstRow;
                    }
                }
                sql = sql + (hasSampleClause ? sampleSql : "");
                return sql;
            }
        });
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return true;
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isBindLimitParametersInReverseOrder() {
        return true;
    }


}
