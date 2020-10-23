
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
    /**
     * 数据库方言，也是数据库标示符
     */
    private String dialect;
    /**
     * 自定义数据库方言类
     */
    private String dialectClassName;
    /**
     * 如果不配置dialect，会走自动提取数据源的逻辑 {MyBatis#getDatabaseId()}
     * 其中有一个步骤是从 MyBatis Configuration中获取，
     * 而Configuration中的database id 则是在初始化Configuration时，内部自己根据数据源获取的，
     * 那么在多数据源的场景下，会出现获取的是第一个连接到的数据库。
     *
     * 加入该配置项的目的是，在多数据源的情况下，通过设置为 false，来禁用从Configuration中获取
     */
    private boolean extractDialectUseNativeEnabled = true;
    /**
     * 是否对修改后的SQL进行缓存
     */
    private boolean cacheInstrumentedSql = false;
    /**
     * 缓存初始容量
     */
    private int cacheInitialCapacity = 1000;
    /**
     * 缓存的最大容量
     */
    private int cacheMaxCapacity = Integer.MAX_VALUE;
    /**
     * 缓存数据的默认过期时间
     */
    private int cacheExpireAfterRead = 5 * 60; //unit: s
    /**
     * 如果有子查询分页的情形，分页部分的开始标记
     */
    private String subqueryPagingStartFlag = "[PAGING_START]";
    /**
     * 如果有子查询分页的情形，分页部分的结束标记
     */
    private String subqueryPagingEndFlag = "[PAGING_END]";
    /**
     * 修改SQL的实现库
     */
    private String instrumentation = "jsqlparser";
    /**
     * 是否开启对 like 参数进行 % _ 转义
     */
    private boolean escapeLikeParameter = false;

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

    public boolean isExtractDialectUseNativeEnabled() {
        return extractDialectUseNativeEnabled;
    }

    public void setExtractDialectUseNativeEnabled(boolean extractDialectUseNativeEnabled) {
        this.extractDialectUseNativeEnabled = extractDialectUseNativeEnabled;
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

    public boolean isEscapeLikeParameter() {
        return escapeLikeParameter;
    }

    public void setEscapeLikeParameter(boolean escapeLikeParameter) {
        this.escapeLikeParameter = escapeLikeParameter;
    }
}
