
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

import com.jn.sqlhelper.dialect.internal.limit.TopLimitHandler;

import java.sql.CallableStatement;
import java.sql.SQLException;


public class CacheDialect extends AbstractDialect {
    public CacheDialect() {
        super();
        setLimitHandler(new TopLimitHandler() {
            @Override
            public String getLimitString(String sql, boolean hasOffset) {
                if (hasOffset) {
                    throw new UnsupportedOperationException("query result offset is not supported");
                }


                int insertionPoint = sql.startsWith("select distinct") ? 15 : 6;


                return new StringBuilder(sql.length() + 8).append(sql).insert(insertionPoint, " TOP ? ").toString();
            }
        });
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return false;
    }

    @Override
    public boolean isSupportsVariableLimit() {
        return true;
    }

    @Override
    public boolean isBindLimitParametersFirst() {
        return true;
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
