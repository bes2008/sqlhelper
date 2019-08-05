
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

package com.jn.sqlhelper.dialect.conf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SQLInstrumentConfig {
    private String dialect;
    private String dialectClassName;
    private boolean cacheInstrumentedSql = false;


    public String getDialect() {
        return this.dialect;
    }

    public void setDialect(final String dialect) {
        this.dialect = dialect;
    }

    public String getDialectClassName() {
        return this.dialectClassName;
    }

    public void setDialectClassName(final String dialectClassName) {
        this.dialectClassName = dialectClassName;
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.toJson(this);
    }

    public boolean isCacheInstrumentedSql() {
        return cacheInstrumentedSql;
    }

    public void setCacheInstrumentedSql(boolean cacheInstrumentedSql) {
        this.cacheInstrumentedSql = cacheInstrumentedSql;
    }
}
