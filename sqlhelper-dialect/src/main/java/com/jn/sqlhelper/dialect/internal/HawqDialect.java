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

import com.jn.sqlhelper.dialect.internal.limit.LimitOffsetLimitHandler;

/**
 * http://hawq.apache.org/docs/userguide/2.3.0.0-incubating/reference/sql/SELECT.html
 *
 * SELECT [ALL | DISTINCT [ON (<expression> [, ...])]]
 * | <expression> [[AS] <output_name>] [, ...]
 [FROM <from_item> [, ...]]
 [WHERE <condition>]
 [GROUP BY <grouping_element> [, ...]]
 [HAVING <condition> [, ...]]
 [WINDOW <window_name> AS (<window_specification>)]
 [{UNION | INTERSECT | EXCEPT} [ALL] <select>]
 [ORDER BY <expression> [ASC | DESC | USING <operator>] [, ...]]
 [LIMIT {<count> | ALL}]
 [OFFSET <start>]
 */
public class HawqDialect extends AbstractDialect {
    public HawqDialect(){
        super();
        setLimitHandler(new LimitOffsetLimitHandler());
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
        return true;
    }
}
