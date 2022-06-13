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

import com.jn.sqlhelper.dialect.internal.limit.OffsetFetchFirstOnlyLimitHandler;

/**
 * https://docs.snowflake.net/manuals/sql-reference/constructs/limit.html
 * <p>
 * support two limit syntax:
 * SELECT ...
 * FROM ...
 * [ ORDER BY ... ]
 * LIMIT <count> [ OFFSET <start> ]
 * [ ... ]
 * <p>
 * -- ANSI syntax
 * SELECT ...
 * FROM ...
 * [ ORDER BY ... ]
 * [ OFFSET <start> ] [ { ROW | ROWS } ] FETCH [ { FIRST | NEXT } ] <count> [ { ROW | ROWS } ] [ ONLY ]
 * [ ... ]
 */
public class SnowflakeDialect extends AbstractDialect {
    public SnowflakeDialect() {
        super();
        setLimitHandler(new OffsetFetchFirstOnlyLimitHandler());
    }


    @Override
    public boolean isSupportsLimitOffset() {
        return true;
    }
}
