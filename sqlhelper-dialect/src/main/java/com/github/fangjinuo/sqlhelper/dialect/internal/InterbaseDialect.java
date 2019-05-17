
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


public class InterbaseDialect extends AbstractDialect {
    private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
        @Override
        public String processSql(String sql, RowSelection selection) {
            boolean hasOffset = LimitHelper.hasFirstRow(selection);
            return getLimitString(sql, hasOffset);
        }

        @Override
        public String getLimitString(String sql, boolean hasOffset) {
            return sql + " rows ?";
        }
    };


    public InterbaseDialect() {
        super();
        setLimitHandler(LIMIT_HANDLER);
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isBindLimitParametersFirst() {
        return false;
    }

    @Override
    public boolean isBindLimitParametersInReverseOrder() {
        return false;
    }
}
