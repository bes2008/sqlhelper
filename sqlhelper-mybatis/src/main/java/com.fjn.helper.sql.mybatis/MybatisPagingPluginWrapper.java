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

package com.fjn.helper.sql.mybatis;

import com.fjn.helper.sql.dialect.*;
import com.fjn.helper.sql.dialect.conf.SQLInstrumentConfig;
import com.fjn.helper.sql.dialect.pagination.*;
import com.fjn.helper.sql.dialect.parameter.BaseQueryParameters;
import com.fjn.helper.sql.mybatis.plugins.pagination.CustomMybatisParameterHandler;
import com.fjn.helper.sql.mybatis.plugins.pagination.MybatisPagingRequestContext;
import com.fjn.helper.sql.mybatis.plugins.pagination.PaginationPluginConfig;
import com.fjn.helper.sql.util.Initializable;
import com.fjn.helper.sql.util.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@SuppressWarnings({"cast", "unchecked", "rawtypes"})
public class MybatisPagingPluginWrapper {
    private static final Logger logger = LoggerFactory.getLogger(MybatisPagingPluginWrapper.class);
    private static final PagingContextHolder<MybatisPagingRequestContext> PAGING_CONTEXT = (PagingContextHolder<MybatisPagingRequestContext>) PagingContextHolder.getContext();
    private static PagingRequestBasedRowSelectionBuilder rowSelectionBuilder = new PagingRequestBasedRowSelectionBuilder();

    private static PaginationPluginConfig pluginConfig = new PaginationPluginConfig();
    private static SQLStatementInstrumentor instrumentor = new SQLStatementInstrumentor();

    private static final int NON_CACHE_QUERY_METHOD_PARAMS = 4;
    private ExecutorInterceptor executorInterceptor;

    static {
        PAGING_CONTEXT.setContextClass(MybatisPagingRequestContext.class);
    }

    public void initPlugin(final PaginationPluginConfig pluginConfig) {
        initPlugin(pluginConfig, null);
    }

    public void initPlugin(final PaginationPluginConfig pluginConfig, SQLInstrumentConfig instrumentConfig) {
        logger.info("Init mybatis pagination plugin with plugin config:{}, sql instrumentor config: {}", pluginConfig, instrumentConfig);
        if (Strings.isBlank(pluginConfig.getDialect())) {
            pluginConfig.setDialect(null);
        }
        if (Strings.isBlank(pluginConfig.getDialectClassName())) {
            pluginConfig.setDialectClassName(null);
        }
        MybatisPagingPluginWrapper.pluginConfig = pluginConfig;
        setInstrumentor(new SQLStatementInstrumentor());
        instrumentConfig = instrumentConfig == null ? new SQLInstrumentConfig() : instrumentConfig;
        instrumentor.setConfig(instrumentConfig);
        this.getPlugins().forEach(plugin -> {
            if (plugin instanceof Initializable) {
                ((Initializable) plugin).init();
            }
        });
    }

    public MybatisPagingPluginWrapper() {
        this.executorInterceptor = new ExecutorInterceptor();
    }

    public List<Interceptor> getPlugins() {
        final List<Interceptor> interceptors = new ArrayList<Interceptor>();
        interceptors.add(this.executorInterceptor);
        return interceptors;
    }

    public void setInstrumentor(final SQLStatementInstrumentor instrumentor) {
        if(instrumentor!=null) {
            MybatisPagingPluginWrapper.instrumentor = instrumentor;
        }
    }

    public static SQLStatementInstrumentor getInstrumentor() {
        return instrumentor;
    }

    static class BoundSqls {
        private static Field additionalParametersField;

        static Map<String, Object> getAdditionalParameter(final BoundSql boundSql) {
            if (BoundSqls.additionalParametersField != null) {
                try {
                    return (Map<String, Object>) BoundSqls.additionalParametersField.get(boundSql);
                } catch (IllegalAccessException ex) {
                    // Noop
                }
            }
            return Collections.emptyMap();
        }

        static {
            try {
                (BoundSqls.additionalParametersField = BoundSql.class.getDeclaredField("additionalParameters")).setAccessible(true);
            } catch (NoSuchFieldException ex) {
                // Noop
            }
        }
    }

    @Intercepts({@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}), @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})})
    public static class ExecutorInterceptor implements Interceptor, Initializable {
        private Cache<String, MappedStatement> countStatementCache;
        private String countSuffix;

        public ExecutorInterceptor() {
            this.countSuffix = "_COUNT";
        }

        @Override
        public void init() {
            if (MybatisPagingPluginWrapper.pluginConfig.enableCountCache()) {
                this.countStatementCache = CacheBuilder.newBuilder()
                        .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                        .expireAfterWrite(pluginConfig.getCountCacheExpireInSeconds(), TimeUnit.SECONDS)
                        .initialCapacity(pluginConfig.getCountCacheInitCapacity())
                        .maximumSize(pluginConfig.getCountCacheMaxCapacity()).build();
                this.countSuffix = (Strings.isBlank(MybatisPagingPluginWrapper.pluginConfig.getCountSuffix()) ? "_COUNT" : pluginConfig.getCountSuffix().trim());
            }
        }

        @Override
        public Object plugin(final Object target) {
            if (target instanceof Executor) {
                logger.debug("wrap mybatis executor {}", target.getClass());
                return Plugin.wrap(target, this);
            }
            return target;
        }

        @Override
        public void setProperties(final Properties properties) {
            logger.info("{}", properties);
        }

        @Override
        public Object intercept(final Invocation invocation) {
            if (logger.isDebugEnabled()) {
                logger.debug("{}", invocation);
            }
            final Object[] args = invocation.getArgs();
            final MappedStatement ms = (MappedStatement) args[0];
            final Object parameter = args[1];
            final RowBounds rowBounds = (RowBounds) args[2];
            final ResultHandler resultHandler = (ResultHandler) args[3];
            final Executor executor = (Executor) invocation.getTarget();
            BoundSql boundSql;
            CacheKey cacheKey;
            if (args.length == NON_CACHE_QUERY_METHOD_PARAMS) {
                boundSql = ms.getBoundSql(parameter);
                cacheKey = executor.createCacheKey(ms, parameter, rowBounds, boundSql);
            } else {
                cacheKey = (CacheKey) args[4];
                boundSql = (BoundSql) args[5];
            }
            Object rs = null;
            try {
                if (this.beginIfSupportsLimit(ms)) {
                    final PagingRequest request = PAGING_CONTEXT.getPagingRequest();
                    final PagingResult result = new PagingResult();
                    request.setResult(result);
                    boolean needQuery = true;
                    try {
                        if (this.needCount(request)) {
                            final int count = this.executeCount(ms, parameter, rowBounds, resultHandler, executor, boundSql);
                            if (count == 0) {
                                needQuery = false;
                            }
                            result.setTotal(count);
                        }
                    } catch (Throwable ex) {
                        logger.error(ex.getMessage(), ex);
                    } finally {
                        if (needQuery) {
                            List items = this.executeQuery(ms, parameter, rowBounds, resultHandler, executor, boundSql, cacheKey);
                            if (items == null) {
                                items = new ArrayList();
                            }
                            result.setPageSize(request.getPageSize());
                            result.setPageNo(request.getPageNo());
                            result.setItems(items);
                            rs = items;
                        } else {
                            rs = new ArrayList();
                        }
                    }
                } else {
                    rs = invocation.proceed();
                }
            } catch (Throwable ex2) {
                logger.error(ex2.getMessage(), ex2);
            } finally {
                PAGING_CONTEXT.remove();
                instrumentor.finish();
            }
            return rs;
        }

        private boolean beginIfSupportsLimit(final MappedStatement statement) {
            if (statement.getStatementType() != StatementType.PREPARED || SqlCommandType.SELECT != statement.getSqlCommandType() || PAGING_CONTEXT.getPagingRequest() == null || !PAGING_CONTEXT.getPagingRequest().isValidRequest()) {
                PAGING_CONTEXT.remove();
                return false;
            }
            final String databaseId = getDatabaseId(statement);
            return instrumentor.beginIfSupportsLimit(databaseId);
        }

        private String getDatabaseId(final MappedStatement ms) {
            PagingRequest request = PAGING_CONTEXT.getPagingRequest();
            String databaseId = request.getDialect();
            if (databaseId == null) {
                databaseId = ms.getDatabaseId();
            }
            if (databaseId == null) {
                databaseId = pluginConfig.getDialect();
            }
            if (databaseId == null) {
                return ms.getConfiguration().getDatabaseId();
            }
            return databaseId;
        }

        private List executeQuery(final MappedStatement ms, final Object parameter, final RowBounds rowBounds, final ResultHandler resultHandler, final Executor executor, final BoundSql boundSql, final CacheKey cacheKey) throws SQLException {
            final PagingRequest request = PAGING_CONTEXT.getPagingRequest();
            final RowSelection rowSelection = rowSelectionBuilder.build(request);
            PAGING_CONTEXT.setRowSelection(rowSelection);
            final String pageSql = instrumentor.instrumentSql(boundSql.getSql(), rowSelection);
            final BoundSql pageBoundSql = new BoundSql(ms.getConfiguration(), pageSql, boundSql.getParameterMappings(), parameter);
            final Map<String, Object> additionalParameters = BoundSqls.getAdditionalParameter(boundSql);
            for (final String key : additionalParameters.keySet()) {
                pageBoundSql.setAdditionalParameter(key, additionalParameters.get(key));
            }
            return executor.query(ms, parameter, RowBounds.DEFAULT, resultHandler, cacheKey, pageBoundSql);
        }

        private int executeCount(final MappedStatement ms, final Object parameter, final RowBounds rowBounds, final ResultHandler resultHandler, final Executor executor, final BoundSql boundSql) throws Throwable {
            final MybatisPagingRequestContext requestContext = PAGING_CONTEXT.get();
            final PagingRequest request = PAGING_CONTEXT.getPagingRequest();
            final String countStatementId = this.getCountStatementId(request, ms.getId());
            int count;
            BoundSql countBoundSql = null;
            try {
                MappedStatement countStatement = this.extractCountStatementFromConfiguration(ms.getConfiguration(), countStatementId);
                if (countStatement != null) {
                    final CacheKey countKey = executor.createCacheKey(countStatement, parameter, RowBounds.DEFAULT, boundSql);
                    countBoundSql = countStatement.getBoundSql(parameter);
                    requestContext.setCountSql(countBoundSql);
                    final Object countResultList = executor.query(countStatement, parameter, RowBounds.DEFAULT, resultHandler, countKey, countBoundSql);
                    count = ((Number) ((List) countResultList).get(0)).intValue();
                } else {
                    countStatement = this.customCountStatement(ms, countStatementId);
                    final Map<String, Object> additionalParameters = BoundSqls.getAdditionalParameter(boundSql);
                    final CacheKey countKey2 = executor.createCacheKey(countStatement, parameter, RowBounds.DEFAULT, boundSql);
                    final String countSql = instrumentor.countSql(boundSql.getSql());
                    countBoundSql = new BoundSql(countStatement.getConfiguration(), countSql, boundSql.getParameterMappings(), parameter);
                    requestContext.setCountSql(countBoundSql);
                    for (final String key : additionalParameters.keySet()) {
                        countBoundSql.setAdditionalParameter(key, additionalParameters.get(key));
                    }
                    final Object countResultList2 = executor.query(countStatement, parameter, RowBounds.DEFAULT, resultHandler, countKey2, countBoundSql);
                    count = ((Number) ((List) countResultList2).get(0)).intValue();
                }
            } catch (Throwable ex) {
                if (countBoundSql != null) {
                    logger.error("error occur when execute count sql [{}], error: {}", countBoundSql.getSql(), ex.getMessage(), ex);
                }
                throw ex;
            } finally {
                requestContext.setCountSql(null);
            }
            return count;
        }

        private boolean needCount(final PagingRequest request) {
            if (request.getCount() == null) {
                return pluginConfig.isCount();
            }
            return Boolean.TRUE.compareTo(request.getCount()) == 0;
        }

        private String getCountStatementId(final PagingRequest request, final String currentSqlId) {
            if (!Strings.isBlank(request.getCountSqlId())) {
                return request.getCountSqlId();
            }
            return currentSqlId + this.countSuffix;
        }

        private MappedStatement extractCountStatementFromConfiguration(final Configuration configuration, final String countStatementId) {
            MappedStatement mappedStatement = null;
            try {
                mappedStatement = configuration.getMappedStatement(countStatementId, false);
            } catch (Throwable t) {
                // NOOP
            }
            return mappedStatement;
        }

        private MappedStatement customCountStatement(final MappedStatement ms, final String countStatementId) {
            MappedStatement countStatement = pluginConfig.enableCountCache() ? this.countStatementCache.getIfPresent(countStatementId) : null;
            if (countStatement == null) {
                final MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), countStatementId, ms.getSqlSource(), ms.getSqlCommandType());
                builder.resource(ms.getResource());
                builder.fetchSize(ms.getFetchSize());
                builder.statementType(ms.getStatementType());
                builder.keyGenerator(ms.getKeyGenerator());
                if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
                    final StringBuilder keyProperties = new StringBuilder();
                    for (final String keyProperty : ms.getKeyProperties()) {
                        keyProperties.append(keyProperty).append(",");
                    }
                    keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
                    builder.keyProperty(keyProperties.toString());
                }
                builder.timeout(ms.getTimeout());
                builder.parameterMap(ms.getParameterMap());
                final List<ResultMap> resultMaps = new ArrayList<>();
                final ResultMap resultMap = new ResultMap.Builder(ms.getConfiguration(), ms.getId(), Long.class, Collections.emptyList()).build();
                resultMaps.add(resultMap);
                builder.resultMaps(resultMaps);
                builder.resultSetType(ms.getResultSetType());
                builder.cache(ms.getCache());
                builder.flushCacheRequired(ms.isFlushCacheRequired());
                builder.useCache(ms.isUseCache());
                countStatement = builder.build();
                if (MybatisPagingPluginWrapper.pluginConfig.enableCountCache()) {
                    this.countStatementCache.put(countStatementId, countStatement);
                }
            }
            return countStatement;
        }
    }
}
