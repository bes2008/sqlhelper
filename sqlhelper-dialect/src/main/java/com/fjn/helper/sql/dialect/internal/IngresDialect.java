/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the LGPL, Version 2.1 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fjn.helper.sql.dialect.internal;

import com.fjn.helper.sql.dialect.internal.limit.LimitHelper;
import com.fjn.helper.sql.dialect.RowSelection;
import com.fjn.helper.sql.dialect.internal.limit.AbstractLimitHandler;
import com.fjn.helper.sql.dialect.internal.limit.LimitHandler;


public class IngresDialect extends AbstractDialect {
    private static final LimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
        @Override
        public String processSql(String sql, RowSelection selection) {
            String soff = " offset " + selection.getOffset();
            String slim = " fetch first " + getMaxOrLimit(selection) + " rows only";

            StringBuilder sb = new StringBuilder(sql.length() + soff.length() + slim.length()).append(sql);
            if (LimitHelper.hasFirstRow(selection)) {
                sb.append(soff);
            }
            if (LimitHelper.hasMaxRows(selection)) {
                sb.append(slim);
            }
            return sb.toString();
        }

        @Override
        public String getLimitString(String querySelect, int offset, int limit) {
            StringBuilder soff = new StringBuilder(" offset " + offset);
            StringBuilder slim = new StringBuilder(" fetch first " + limit + " rows only");

            StringBuilder sb = new StringBuilder(querySelect.length() + soff.length() + slim.length()).append(querySelect);
            if (offset > 0) {
                sb.append(soff);
            }
            if (limit > 0) {
                sb.append(slim);
            }
            return sb.toString();
        }
    };


    public IngresDialect() {
        super();
        setLimitHandler(LIMIT_HANDLER);
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return true;
    }
}
