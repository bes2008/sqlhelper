
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

package com.github.fangjinuo.sqlhelper.dialect.internal;

import com.github.fangjinuo.sqlhelper.dialect.RowSelection;
import com.github.fangjinuo.sqlhelper.dialect.internal.limit.AbstractLimitHandler;
import com.github.fangjinuo.sqlhelper.dialect.internal.limit.LimitHelper;

import java.sql.CallableStatement;
import java.sql.SQLException;


public class DB2Dialect extends AbstractDialect {
    public DB2Dialect() {
        super();
        setLimitHandler(new AbstractLimitHandler() {
            @Override
            public String processSql(String sql, RowSelection selection) {
                if (DB2Dialect.this.isDB2_400OrNewer()) {
                    if (LimitHelper.hasFirstRow(selection)) {
                        return "select * from ( select inner2_.*, rownumber() over(order by order of inner2_) as rownumber_ from ( " + sql + " fetch first " + getMaxOrLimit(selection) + " rows only ) as inner2_ ) as inner1_ where rownumber_ > " + selection.getOffset() + " order by rownumber_";
                    }
                    return sql + " fetch first " + getMaxOrLimit(selection) + " rows only";
                }
                int limit = selection.getLimit().intValue();
                if (limit == 0) {
                    return sql;
                }
                return sql + " fetch first " + limit + " rows only ";
            }
        });
    }


    private boolean isDB2_400OrNewer() {
        return true;
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isSupportsVariableLimit() {
        return false;
    }

    @Override
    public boolean isUseMaxForLimit() {
        return true;
    }

    @Override
    public int registerResultSetOutParameter(CallableStatement statement, int col)
            throws SQLException {
        return col;
    }
}
