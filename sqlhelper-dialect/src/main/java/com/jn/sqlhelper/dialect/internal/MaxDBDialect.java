
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

import com.jn.langx.annotation.Name;
import com.jn.sqlhelper.dialect.pagination.RowSelection;
import com.jn.sqlhelper.dialect.internal.limit.AbstractLimitHandler;
import com.jn.sqlhelper.dialect.internal.limit.LimitHelper;

@Name("maxdb")
public class MaxDBDialect extends AbstractDialect {

    public MaxDBDialect() {
        super();
        setLimitHandler(new AbstractLimitHandler() {
            @Override
            public String processSql(String sql, boolean isSubquery, boolean useLimitVariable, RowSelection selection) {
                boolean hasOffset = LimitHelper.hasFirstRow(selection);
                if(useLimitVariable && isUseLimitInVariableMode(isSubquery)) {
                    sql = "select * from (" + sql + ") where rowno < ? ";
                    if (hasOffset) {
                        sql = sql + " and rowno >= ?";
                    }
                }else{
                    int firstRow = (int)convertToFirstRowValue(LimitHelper.getFirstRow(selection));
                    int lastRow = getMaxOrLimit(selection);
                    sql = "select * from (" + sql + ") where rowno < " + lastRow;
                    if (hasOffset) {
                        sql = sql + " and rowno >= " + firstRow;
                    }
                }
                return sql;
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
        return false;
    }
}
