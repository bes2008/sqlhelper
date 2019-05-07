
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

import com.fjn.helper.sql.dialect.internal.limit.FirstLimitHandler;
import com.fjn.helper.sql.dialect.internal.urlparser.InformixUrlParser;

import java.util.Locale;


public class InformixDialect extends AbstractDialect {
    public InformixDialect() {
        super();
        setUrlParser(new InformixUrlParser());
        setLimitHandler(new FirstLimitHandler() {
            @Override
            public String getLimitString(String querySelect, int offset, int limit) {
                if (offset > 0) {
                    throw new UnsupportedOperationException("query result offset is not supported");
                }
                return new StringBuilder(querySelect.length() + 8).append(querySelect).insert(querySelect.toLowerCase(Locale.ROOT).indexOf("select") + 6, " first " + limit).toString();
            }
        });
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isUseMaxForLimit() {
        return true;
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return false;
    }

    @Override
    public boolean isSupportsVariableLimit() {
        return false;
    }
}
