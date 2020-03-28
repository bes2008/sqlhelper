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
import com.jn.sqlhelper.dialect.urlparser.UrlParser;

/**
 * https://bloomberg.github.io/comdb2/sql.html#select-statement
 * <p>
 * supports 2 styles limit syntax:
 * 1) limit $limit offset $offset
 * 2) limit $offset, $limit
 * <p>
 * We use 1)
 *
 * https://bloomberg.github.io/comdb2/sql.html
 */
public class ComDB2Dialect extends AbstractDialect {
    @Override
    protected void setUrlParser(UrlParser urlParser) {
        super.setUrlParser(urlParser);
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
