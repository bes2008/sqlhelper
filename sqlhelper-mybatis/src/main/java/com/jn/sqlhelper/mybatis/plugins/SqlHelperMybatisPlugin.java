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
import com.jn.langx.util.collection.PropertiesAccessor;
import com.jn.sqlhelper.dialect.SQLStatementInstrumentor;
import com.jn.sqlhelper.dialect.conf.SQLInstrumentConfig;
import com.jn.sqlhelper.mybatis.plugins.likeescape.LikeParameterEscapeHandler;
import com.jn.sqlhelper.mybatis.plugins.pagination.PaginationHandler;
import com.jn.sqlhelper.mybatis.plugins.pagination.PaginationConfig;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "queryCursor", args = {MappedStatement.class, Object.class, RowBounds.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class SqlHelperMybatisPlugin implements Interceptor, Initializable {
    private static final Logger logger = LoggerFactory.getLogger(SqlHelperMybatisPlugin.class);
    private PaginationConfig pluginConfig = new PaginationConfig();
    private static SQLStatementInstrumentor instrumentor = new SQLStatementInstrumentor();
    private boolean inited = false;
    private Map<String, Handler> handlerRegistry = new HashMap<String, Handler>();

    @Override
    public void init() throws InitializationException {
        if (!inited) {
            instrumentor.init();

            LikeParameterEscapeHandler likeParameterEscapeHandler = new LikeParameterEscapeHandler();
            handlerRegistry.put("likeEscape", likeParameterEscapeHandler);

            PaginationHandler paginationHandler = new PaginationHandler();
            setPaginationPluginConfig(pluginConfig);
            paginationHandler.init();
            handlerRegistry.put("pagination", paginationHandler);

            ExecutorInvocationSinkHandler sinkHandler = new ExecutorInvocationSinkHandler();
            handlerRegistry.put("sink", sinkHandler);

        }
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
            if (!pipeline.hadOutbound()) {
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
        logger.info("{}", properties);
        if (!inited) {
            PropertiesAccessor accessor = new PropertiesAccessor(properties);
            PaginationConfig pluginConfig = parsePaginationConfig(accessor);
            SQLInstrumentConfig instrumentConfig = parseInstrumentorConfig(accessor);
            setInstrumentorConfig(instrumentConfig);
            setPaginationPluginConfig(pluginConfig);
            init();
        }
    }

    public void setPaginationPluginConfig(PaginationConfig config) {
        this.pluginConfig = config;
    }

    public void setInstrumentorConfig(SQLInstrumentConfig config) {
        instrumentor.setConfig(config);
    }

    public static SQLStatementInstrumentor getInstrumentor() {
        return instrumentor;
    }


    private PaginationConfig parsePaginationConfig(PropertiesAccessor accessor) {
        PaginationConfig pluginConfig = new PaginationConfig();
        String paginationPluginConfigPrefix = "sqlhelper.mybatis.pagination.";

        pluginConfig.setCount(accessor.getBoolean(paginationPluginConfigPrefix + "count", pluginConfig.isCount()));
        pluginConfig.setCountCacheExpireInSeconds(accessor.getInteger(paginationPluginConfigPrefix + "countCacheExpireInSeconds", pluginConfig.getCountCacheExpireInSeconds()));
        pluginConfig.setCountCacheInitCapacity(accessor.getInteger(paginationPluginConfigPrefix + "countCacheInitCapacity", pluginConfig.getCountCacheInitCapacity()));
        pluginConfig.setCountCacheMaxCapacity(accessor.getInteger(paginationPluginConfigPrefix + "countCacheMaxCapacity", pluginConfig.getCountCacheMaxCapacity()));
        pluginConfig.setCountSuffix(accessor.getString(paginationPluginConfigPrefix + "countSuffix", pluginConfig.getCountSuffix()));
        pluginConfig.setDefaultPageSize(accessor.getInteger(paginationPluginConfigPrefix + "defaultPageSize", pluginConfig.getDefaultPageSize()));
        pluginConfig.setUseLastPageIfPageNoOut(accessor.getBoolean(paginationPluginConfigPrefix + "useLastPageIfPageNoOut", pluginConfig.isUseLastPageIfPageNoOut()));

        return pluginConfig;
    }

    private SQLInstrumentConfig parseInstrumentorConfig(PropertiesAccessor accessor) {
        SQLInstrumentConfig instrumentConfig = new SQLInstrumentConfig();
        String instrumentorConfigPrefix = "sqlhelper.mybatis.instrumentor.";
        instrumentConfig.setDialect(accessor.getString(instrumentorConfigPrefix + "dialect", instrumentConfig.getDialect()));
        instrumentConfig.setDialectClassName(accessor.getString(instrumentorConfigPrefix + "dialectClassName", instrumentConfig.getDialectClassName()));
        instrumentConfig.setCacheInstrumentedSql(accessor.getBoolean(instrumentorConfigPrefix + "cacheInstruemtedSql", false));
        return instrumentConfig;
    }
}
