
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

import com.jn.sqlhelper.dialect.pagination.RowSelection;
import com.jn.sqlhelper.dialect.internal.limit.AbstractLimitHandler;
import com.jn.sqlhelper.dialect.internal.limit.LimitHelper;

/**
 * http://www.firebirdsql.org/file/documentation/reference_manuals/fblangref25-en/html/fblangref25-dml-select.html#fblangref25-dml-select-first-skip
 * http://www.firebirdsql.org/file/documentation/reference_manuals/fblangref25-en/html/fblangref25-commons-predicates.html
 */
public class FirebirdDialect extends InterbaseDialect {

    public FirebirdDialect() {
        super();
        setLimitHandler(new AbstractLimitHandler() {
            @Override
            public String processSql(String sql, boolean isSubQuery, RowSelection selection) {
                boolean hasOffset = LimitHelper.hasFirstRow(selection);
                return getLimitString(sql,isSubQuery, hasOffset);
            }

            @Override
            public String getLimitString(String sql, boolean isSubQuery, boolean hasOffset) {
                return new StringBuilder(sql.length() + 20).append(sql).insert(6, hasOffset ? " first ? skip ?" : " first ?").toString();
            }
        });

    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isBindLimitParametersFirst() {
        return true;
    }

    @Override
    public boolean isBindLimitParametersInReverseOrder() {
        return true;
    }
}
