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
import com.jn.langx.pipeline.*;
import com.jn.langx.text.properties.PropertiesAccessor;
import com.jn.langx.util.ClassLoaders;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.reflect.Reflects;
import com.jn.sqlhelper.dialect.SqlRequestContextHolder;
import com.jn.sqlhelper.dialect.instrument.SQLInstrumentorConfig;
import com.jn.sqlhelper.dialect.instrument.SQLStatementInstrumentor;
import com.jn.sqlhelper.dialect.pagination.PagingRequestContext;
import com.jn.sqlhelper.dialect.pagination.PagingRequestContextHolder;
import com.jn.sqlhelper.mybatis.plugins.likeescape.LikeParameterEscapeHandler;
import com.jn.sqlhelper.mybatis.plugins.pagination.PaginationConfig;
import com.jn.sqlhelper.mybatis.plugins.pagination.PaginationHandler;
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
    private PaginationConfig paginationConfig = new PaginationConfig();
    private static SQLStatementInstrumentor instrumentor = new SQLStatementInstrumentor();
    private boolean inited = false;
    private Map<String, Handler> handlerRegistry = new HashMap<String, Handler>();

    @Override
    public void init() throws InitializationException {
        if (!inited) {
            instrumentor.init();
            DebugHandler debugHandler = new DebugHandler();
            handlerRegistry.put("debug", debugHandler);
            LikeParameterEscapeHandler likeParameterEscapeHandler = new LikeParameterEscapeHandler(instrumentor.getConfig().isEscapeLikeParameter());
            handlerRegistry.put("likeEscape", likeParameterEscapeHandler);
            PaginationHandler paginationHandler = new PaginationHandler();
            paginationHandler.setPaginationConfig(this.paginationConfig);
            paginationHandler.init();
            handlerRegistry.put("pagination", paginationHandler);
            if (paginationConfig.isPageHelperCompatible()) {
                try {
                    Class<Handler> pageHelperHandlerClass = ClassLoaders.loadClass(paginationConfig.getPageHelperHandlerClass(), SqlHelperMybatisPlugin.class);
                    Handler pageHelperHandler = Reflects.<Handler>newInstance(pageHelperHandlerClass);
                    handlerRegistry.put(PageHelperCompibles.pageHelperRequestFlag, pageHelperHandler);
                } catch (ClassNotFoundException ex) {
                    logger.info("Can't find the pageHelperHandler, maybe it is unnecessary");
                    this.paginationConfig.setPageHelperCompatible(false);
                }
            }

            ExecutorInvocationSinkHandler sinkHandler = new ExecutorInvocationSinkHandler();
            handlerRegistry.put("sink", sinkHandler);
            inited = true;
        }
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        ExecutorInvocation executorInvocation = new ExecutorInvocation(invocation);
        try {
            Pipeline<ExecutorInvocation> pipeline = createPipeline(executorInvocation);
            pipeline.inbound();
            if (!pipeline.hadOutbound()) {
                Pipelines.outbound(pipeline);
            }
            return executorInvocation.getResult();
        } finally {
            if (!NestedStatements.isNestedStatement(executorInvocation.getMappedStatement())) {
                SqlRequestContextHolder.getInstance().clear();
            }
        }
    }

    private Pipeline<ExecutorInvocation> createPipeline(ExecutorInvocation executorInvocation) {
        Handler debugHandler = handlerRegistry.get("debug");
        Handler sinkHandler = handlerRegistry.get("sink");
        List<Handler> handlers = Collects.emptyArrayList();
        if ("query".equals(executorInvocation.getMethodName())) {
            handlers.add(handlerRegistry.get("likeEscape"));
            handlers.add(handlerRegistry.get("pagination"));
            Handler pageHelperHandler = handlerRegistry.get(PageHelperCompibles.pageHelperRequestFlag);
            if (this.paginationConfig.isPageHelperCompatible() && pageHelperHandler != null) {
                if (PagingRequestContextHolder.getContext().isPagingRequest()) {
                    PagingRequestContext context = PagingRequestContextHolder.getContext().get();
                    boolean isPageHelperRequest = context.getBoolean(PageHelperCompibles.pageHelperRequestFlag, false);
                    if (isPageHelperRequest) {
                        handlers.add(pageHelperHandler);
                    }
                }
            }
        }
//        if("update".equals(executorInvocation.getMethodName())){
//            handlers.add(handlerRegistry.get("likeEscape"));
//        }

        DefaultPipeline<ExecutorInvocation> pipeline = Pipelines.newPipeline(debugHandler, sinkHandler, handlers);
        pipeline.bindTarget(executorInvocation);
        return pipeline;
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
            PaginationConfig paginationConfig = parsePaginationConfig(accessor);
            SQLInstrumentorConfig instrumentConfig = parseInstrumentorConfig(accessor);
            setInstrumentorConfig(instrumentConfig);
            setPaginationConfig(paginationConfig);
            init();
        }
    }

    public void setPaginationConfig(PaginationConfig config) {
        this.paginationConfig = config;
    }

    public void setInstrumentorConfig(SQLInstrumentorConfig config) {
        instrumentor.setConfig(config);
    }

    public static SQLStatementInstrumentor getInstrumentor() {
        return instrumentor;
    }


    private PaginationConfig parsePaginationConfig(PropertiesAccessor accessor) {
        PaginationConfig paginationConfig = new PaginationConfig();
        String paginationPluginConfigPrefix = "sqlhelper.mybatis.pagination.";

        paginationConfig.setCount(accessor.getBoolean(paginationPluginConfigPrefix + "count", paginationConfig.isCount()));
        paginationConfig.setCountCacheExpireInSeconds(accessor.getInteger(paginationPluginConfigPrefix + "countCacheExpireInSeconds", paginationConfig.getCountCacheExpireInSeconds()));
        paginationConfig.setCountCacheInitCapacity(accessor.getInteger(paginationPluginConfigPrefix + "countCacheInitCapacity", paginationConfig.getCountCacheInitCapacity()));
        paginationConfig.setCountCacheMaxCapacity(accessor.getInteger(paginationPluginConfigPrefix + "countCacheMaxCapacity", paginationConfig.getCountCacheMaxCapacity()));
        paginationConfig.setCountSuffix(accessor.getString(paginationPluginConfigPrefix + "countSuffix", paginationConfig.getCountSuffix()));
        paginationConfig.setDefaultPageSize(accessor.getInteger(paginationPluginConfigPrefix + "defaultPageSize", paginationConfig.getDefaultPageSize()));
        paginationConfig.setUseLastPageIfPageOut(accessor.getBoolean(paginationPluginConfigPrefix + "useLastPageIfPageOut", accessor.getBoolean(paginationPluginConfigPrefix + "useLastPageIfPageNoOut", paginationConfig.isUseLastPageIfPageOut())));
        paginationConfig.setPageHelperCompatible(accessor.getBoolean(paginationPluginConfigPrefix + "pageHelperCompatible", paginationConfig.isPageHelperCompatible()));
        paginationConfig.setPageHelperHandlerClass(accessor.getString(paginationPluginConfigPrefix + "pageHelperHandlerClass", paginationConfig.getPageHelperHandlerClass()));

        return paginationConfig;
    }

    private SQLInstrumentorConfig parseInstrumentorConfig(PropertiesAccessor accessor) {
        SQLInstrumentorConfig instrumentConfig = new SQLInstrumentorConfig();
        String instrumentorConfigPrefix = "sqlhelper.mybatis.instrumentor.";
        instrumentConfig.setName(accessor.getString(instrumentorConfigPrefix + "name", "undefined"));
        instrumentConfig.setSubqueryPagingStartFlag(accessor.getString(instrumentorConfigPrefix + "subqueryPagingStartFlag", "[PAGING_START]"));
        instrumentConfig.setSubqueryPagingEndFlag(accessor.getString(instrumentorConfigPrefix + "subqueryPagingEndFlag", "[PAGING_END]"));
        instrumentConfig.setDialect(accessor.getString(instrumentorConfigPrefix + "dialect", instrumentConfig.getDialect()));
        instrumentConfig.setInstrumentation(accessor.getString(instrumentorConfigPrefix + "instrumentation", instrumentConfig.getInstrumentation()));
        instrumentConfig.setDialectClassName(accessor.getString(instrumentorConfigPrefix + "dialectClassName", instrumentConfig.getDialectClassName()));
        instrumentConfig.setCacheInstrumentedSql(accessor.getBoolean(instrumentorConfigPrefix + "cacheInstruemtedSql", false));
        instrumentConfig.setEscapeLikeParameter(accessor.getBoolean(instrumentorConfigPrefix + "escapeLikeParameter", false));
        return instrumentConfig;
    }
}
