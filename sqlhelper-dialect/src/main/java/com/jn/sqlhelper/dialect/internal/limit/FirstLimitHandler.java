
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


public class FirstLimitHandler extends LegacyFirstLimitHandler {
    @Override
    public String processSql(String sql, RowSelection selection) {
        boolean hasOffset = LimitHelper.hasFirstRow(selection);
        if (hasOffset) {
            throw new UnsupportedOperationException("query result offset is not supported");
        }
        return super.processSql(sql, selection);
    }
}
