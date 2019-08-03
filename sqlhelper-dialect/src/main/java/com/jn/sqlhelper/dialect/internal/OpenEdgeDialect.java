
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
 *
 reference: https://documentation.progress.com/output/ua/OpenEdge_latest/#page/dmsrf%2Fselect.html%23wwID0E4QHQ
 Select Syntax:
<pre>
 SELECT [ ALL | DISTINCT ] [TOP n]
 { *
 |{table_name|alias} * [ , {table_name| alias} * ]...
 | expr [[ AS ][ ' ] column_title [ ' ]]
 [,  expr [[ AS ][ ' ] column_title [' ]]]...
 }
 FROM table_ref [, table_ref]...[{ NO REORDER }] [ WITH (NOLOCK )]
 [ WHERE search_condition]
 [ GROUP BY [ table ]column_name
 [,[table]column_name ]...
 [HAVING search_condition];

 [ORDER BY ordering_condition]
 [OFFSET offset_value {ROW | ROWS }
 [FETCH {FIRST | NEXT}fetch_value {ROW | ROWS} ONLY ]]
 [WITH locking_hints]
 [FOR UPDATE update_condition];
 *</pre>
 * @author f1194361820
 */
public class OpenEdgeDialect extends AbstractDialect {
    public OpenEdgeDialect(){
        super();
        setLimitHandler(new OffsetFetchFirstOnlyLimitHandler());
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
