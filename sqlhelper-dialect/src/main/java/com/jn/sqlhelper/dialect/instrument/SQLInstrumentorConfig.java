
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

package com.jn.sqlhelper.dialect.instrument;

import com.jn.easyjson.core.JSONBuilderProvider;

public class SQLInstrumentorConfig {
    public static final SQLInstrumentorConfig DEFAULT = new SQLInstrumentorConfig();

    private String name = "undefined";
    private String dialect;
    private String dialectClassName;
    private boolean cacheInstrumentedSql = false;
    private int cacheInitialCapacity = 1000;
    private int cacheMaxCapacity = Integer.MAX_VALUE;
    private int cacheExpireAfterRead = 5 * 60; //unit: s
    private String subqueryPagingStartFlag = "[PAGING_START]";
    private String subqueryPagingEndFlag = "[PAGING_END]";
    private String instrumentation = "jsqlparser";

    public int getCacheInitialCapacity() {
        return cacheInitialCapacity;
    }

    public void setCacheInitialCapacity(int cacheInitialCapacity) {
        if (cacheInitialCapacity > 0) {
            this.cacheInitialCapacity = cacheInitialCapacity;
        }
    }

    public int getCacheMaxCapacity() {
        return cacheMaxCapacity;
    }

    public void setCacheMaxCapacity(int cacheMaxCapacity) {
        if (cacheMaxCapacity >= this.cacheInitialCapacity) {
            this.cacheMaxCapacity = cacheMaxCapacity;
        }
    }

    public int getCacheExpireAfterRead() {
        return cacheExpireAfterRead;
    }

    public void setCacheExpireAfterRead(int cacheExpireAfterRead) {
        if (cacheExpireAfterRead > 0) {
            this.cacheExpireAfterRead = cacheExpireAfterRead;
        }
    }

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
        return JSONBuilderProvider.create().serializeNulls(true).build().toJson(this);
    }

    public boolean isCacheInstrumentedSql() {
        return cacheInstrumentedSql;
    }

    public void setCacheInstrumentedSql(boolean cacheInstrumentedSql) {
        this.cacheInstrumentedSql = cacheInstrumentedSql;
    }

    public String getSubqueryPagingStartFlag() {
        return subqueryPagingStartFlag;
    }

    public void setSubqueryPagingStartFlag(String subqueryPagingStartFlag) {
        this.subqueryPagingStartFlag = subqueryPagingStartFlag;
    }

    public String getSubqueryPagingEndFlag() {
        return subqueryPagingEndFlag;
    }

    public void setSubqueryPagingEndFlag(String subqueryPagingEndFlag) {
        this.subqueryPagingEndFlag = subqueryPagingEndFlag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getInstrumentation() {
        return instrumentation;
    }

    public void setInstrumentation(String instrumentation) {
        this.instrumentation = instrumentation;
    }
}
