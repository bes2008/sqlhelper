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

import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.pipeline.DebugHandler;
import com.jn.langx.pipeline.DefaultPipeline;
import com.jn.langx.pipeline.Handler;
import com.jn.langx.pipeline.Pipelines;
import com.jn.langx.util.collection.Collects;
import com.jn.sqlhelper.mybatis.plugins.likeescape.LikeParameterEscapeHandler;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "queryCursor", args = {MappedStatement.class, Object.class, RowBounds.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class SqlHelperMybatisPlugin implements Interceptor, Initializable {
    private static final Logger logger = LoggerFactory.getLogger(SqlHelperMybatisPlugin.class);

    @Override
    public void init() throws InitializationException {

    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        ExecutorInvocation executorInvocation = new ExecutorInvocation(invocation);
        try {
            DebugHandler debugHandler = new DebugHandler();
            List<Handler> handlers = Collects.<Handler>asList(new LikeParameterEscapeHandler());
            DefaultPipeline<ExecutorInvocation> pipeline = Pipelines.newPipeline(debugHandler, debugHandler, handlers);
            pipeline.bindTarget(executorInvocation);
            pipeline.inbound();
            if(!pipeline.hadOutbound()){
                Pipelines.outbound(pipeline);
            }
            return executorInvocation.getResult();
        } finally {
            // ?
        }

    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            if (logger.isDebugEnabled()) {
                logger.debug("wrap mybatis executor {}", target.getClass());
            }
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
