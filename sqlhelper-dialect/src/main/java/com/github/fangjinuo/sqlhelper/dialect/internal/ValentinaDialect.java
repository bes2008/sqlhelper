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

import com.github.fangjinuo.sqlhelper.dialect.internal.limit.LimitOffsetLimitHandler;

/**
 * http://valentina-db.com/docs/dokuwiki/v5/doku.php?id=valentina:vcomponents:vsql:reference:sqlgrammar:5.0#select_..._limit
 *
 * supports 2 styles limit syntax:
 * 1) limit $limit offset $offset
 * 2) limit $offset, $limit
 *
 * We use 2)
 *
 */
public class ValentinaDialect extends AbstractDialect {
    public ValentinaDialect(){
        super();
        setLimitHandler(new LimitOffsetLimitHandler());
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return true;
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isBindLimitParametersInReverseOrder() {
        return true;
    }
}
