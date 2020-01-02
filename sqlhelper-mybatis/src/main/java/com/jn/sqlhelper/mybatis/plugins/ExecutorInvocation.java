/*
 * Copyright 2020 the original author or authors.
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

package com.jn.sqlhelper.mybatis.plugins;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

public class ExecutorInvocation {
    private Invocation invocation;

    private Executor executor;
    private Object parameter;
    private MappedStatement mappedStatement;
    private RowBounds rowBounds;
    private ResultHandler resultHandler;
    private CacheKey cacheKey;
    private BoundSql boundSql;

    public ExecutorInvocation(Invocation invocation) {
        this.invocation = invocation;
    }

    private void parseQuery() {

    }

    private void parseQueryCursor() {

    }

    private void parseUpdate() {

    }

    public Invocation getInvocation() {
        return invocation;
    }

    public Executor getExecutor() {
        return executor;
    }

    public Object getParameter() {
        return parameter;
    }

    public MappedStatement getMappedStatement() {
        return mappedStatement;
    }

    public RowBounds getRowBounds() {
        return rowBounds;
    }

    public ResultHandler getResultHandler() {
        return resultHandler;
    }

    public CacheKey getCacheKey() {
        return cacheKey;
    }

    public BoundSql getBoundSql() {
        return boundSql;
    }
}
