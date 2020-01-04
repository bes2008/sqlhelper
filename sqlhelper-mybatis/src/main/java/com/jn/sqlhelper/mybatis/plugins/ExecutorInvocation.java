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

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Objects;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Method;

public class ExecutorInvocation {
    @NonNull
    private Invocation invocation;
    @NonNull
    private String methodName;
    @NonNull
    private Executor executor;
    @NonNull
    private Object parameter;
    @NonNull
    private MappedStatement mappedStatement;
    @Nullable
    private RowBounds rowBounds;
    @Nullable
    private ResultHandler resultHandler;
    @Nullable
    private CacheKey cacheKey;
    @NonNull
    private BoundSql boundSql;

    private Object result;

    public ExecutorInvocation(Invocation invocation) {
        this.invocation = invocation;
        this.executor = (Executor) invocation.getTarget();
        parse();
    }

    private void parse() {
        Method method = invocation.getMethod();
        String methodName = method.getName();
        this.methodName = methodName;
        if (methodName.equals("query")) {
            parseQuery();
        } else if (methodName.equals("queryCursor")) {
            parseQueryCursor();
        } else if (methodName.equals("update")) {
            parseUpdate();
        }
    }

    /**
     * lazy parse bound sql
     */
    private BoundSql parseBoundSql() {
        if (Objects.isNull(boundSql)) {
            Object[] args = invocation.getArgs();
            if (this.methodName.equals("query")) {
                if (args.length > 4) {
                    this.cacheKey = (CacheKey) args[4];
                    this.boundSql = (BoundSql) args[5];
                }
            }
            if (Objects.isNull(this.boundSql)) {
                this.boundSql = this.mappedStatement.getBoundSql(parameter);
            }
            if (this.methodName.equals("query")) {
                if (Objects.isNull(cacheKey)) {
                    this.cacheKey = this.executor.createCacheKey(mappedStatement, parameter, rowBounds, boundSql);
                }
            }
        }
        return this.boundSql;
    }

    private void parseQuery() {
        Object[] args = invocation.getArgs();
        this.mappedStatement = (MappedStatement) args[0];
        this.parameter = args[1];
        this.rowBounds = (RowBounds) args[2];
        this.resultHandler = (ResultHandler) args[3];
    }

    private void parseQueryCursor() {
        Object[] args = invocation.getArgs();
        this.mappedStatement = (MappedStatement) args[0];
        this.parameter = args[1];
        this.rowBounds = (RowBounds) args[2];
    }

    private void parseUpdate() {
        Object[] args = invocation.getArgs();
        this.mappedStatement = (MappedStatement) args[0];
        this.parameter = args[1];
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
        return Objects.isNull(boundSql) ? parseBoundSql() : this.boundSql;
    }

    public void setBoundSql(BoundSql boundSql) {
        this.boundSql = boundSql;
    }

    public String getMethodName() {
        return methodName;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
