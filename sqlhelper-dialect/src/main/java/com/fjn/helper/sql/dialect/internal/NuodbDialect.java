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

package com.fjn.helper.sql.dialect.internal;

import com.fjn.helper.sql.dialect.internal.limit.LimitCommaLimitHandler;

/**
 * http://doc.nuodb.com/Latest/Default.htm#SELECT.htm
 * <pre>
 *     SELECT  [ optimizer_hint ]
 [ ALL | DISTINCT ]
 { select_item [ [AS] output_name ] } [, ...]
 FROM from_item
 [ WHERE condition ]
 [ GROUP BY expression [, ...] [ HAVING condition [, ...] ] ]
 [ UNION [ ALL | DISTINCT ] select ]
 [ ORDER BY { order_list [ COLLATE collation_name ] [ ASC | DESC] } [, ...] ]
 [ LIMIT { count [ OFFSET start ] | start [ , count ] }
 [ OFFSET start [ ROW | ROWS ] [ FETCH {FIRST | NEXT}
 count [ROW | ROWS] [ONLY] ] ]
 [ FETCH {FIRST | NEXT } count [ROW | ROWS] [ONLY] ]
 [ FOR UPDATE [NOWAIT] ]
 * </pre>
 *
 *
 *  supports:
 *      1) LIMIT count
        2) LIMIT start, count
        3) LIMIT count OFFSET start

 *  we use LIMIT start, count
 */
public class NuodbDialect extends AbstractDialect {
    public NuodbDialect(){
        super();
        setLimitHandler(new LimitCommaLimitHandler());
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return true;
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }
}
