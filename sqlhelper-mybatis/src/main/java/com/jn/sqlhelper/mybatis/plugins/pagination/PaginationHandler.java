package com.jn.sqlhelper.mybatis.plugins.pagination;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.cache.Cache;
import com.jn.langx.cache.CacheBuilder;
import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.pipeline.AbstractHandler;
import com.jn.langx.pipeline.HandlerContext;
import com.jn.langx.util.Chars;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.PropertiesAccessor;
import com.jn.sqlhelper.dialect.RowSelection;
import com.jn.sqlhelper.dialect.SQLStatementInstrumentor;
import com.jn.sqlhelper.dialect.conf.SQLInstrumentConfig;
import com.jn.sqlhelper.dialect.orderby.OrderBy;
import com.jn.sqlhelper.dialect.pagination.*;
import com.jn.sqlhelper.mybatis.MybatisUtils;
import com.jn.sqlhelper.mybatis.plugins.SqlHelperMybatisPluginContext;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;

public class PaginationHandler extends AbstractHandler implements Initializable {
    @Override
    public void inbound(HandlerContext ctx) throws Throwable {
        super.inbound(ctx);
    }

    private static final Logger logger = LoggerFactory.getLogger(MybatisPaginationPlugin.class);
    private static final int NON_CACHE_QUERY_METHOD_PARAMS = 4;
    private static final PagingRequestContextHolder PAGING_CONTEXT = PagingRequestContextHolder.getContext();
    private PagingRequestBasedRowSelectionBuilder rowSelectionBuilder = new PagingRequestBasedRowSelectionBuilder();
    private PaginationPluginConfig pluginConfig = new PaginationPluginConfig();
    /**
     * count sql cache
     * key: count sql, should not count_id, because the mysql's sql is dynamic
     */
    private Cache<String, MappedStatement> countStatementCache;
    private String countSuffix = "_COUNT";
    private static final String ORDER_BY_SUFFIX = "_orderBy";
    private boolean inited = false;


    @Override
    public String toString() {
        return this.getClass().getCanonicalName();
    }

    @Override
    public void init() {
        if (!inited) {
            rowSelectionBuilder.setDefaultPageSize(pluginConfig.getDefaultPageSize());

            if (pluginConfig.enableCountCache()) {
                this.countStatementCache = CacheBuilder.<String, MappedStatement>newBuilder()
                        .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                        .expireAfterWrite(pluginConfig.getCountCacheExpireInSeconds())
                        .initialCapacity(pluginConfig.getCountCacheInitCapacity())
                        .maxCapacity(pluginConfig.getCountCacheMaxCapacity()).build();
                this.countSuffix = (Strings.isBlank(pluginConfig.getCountSuffix()) ? "_COUNT" : pluginConfig.getCountSuffix().trim());
            }
        }
    }

    private void parseConfig(Properties props, PaginationPluginConfig pluginConfig, SQLInstrumentConfig instrumentConfig) {
        if (props == null) {
            return;
        }
        String paginationPluginConfigPrefix = "sqlhelper.mybatis.pagination.";
        PropertiesAccessor accessor = new PropertiesAccessor(props);
        pluginConfig.setCount(accessor.getBoolean(paginationPluginConfigPrefix + "count", pluginConfig.isCount()));
        pluginConfig.setCountCacheExpireInSeconds(accessor.getInteger(paginationPluginConfigPrefix + "countCacheExpireInSeconds", pluginConfig.getCountCacheExpireInSeconds()));
        pluginConfig.setCountCacheInitCapacity(accessor.getInteger(paginationPluginConfigPrefix + "countCacheInitCapacity", pluginConfig.getCountCacheInitCapacity()));
        pluginConfig.setCountCacheMaxCapacity(accessor.getInteger(paginationPluginConfigPrefix + "countCacheMaxCapacity", pluginConfig.getCountCacheMaxCapacity()));
        pluginConfig.setCountSuffix(accessor.getString(paginationPluginConfigPrefix + "countSuffix", pluginConfig.getCountSuffix()));
        pluginConfig.setDefaultPageSize(accessor.getInteger(paginationPluginConfigPrefix + "defaultPageSize", pluginConfig.getDefaultPageSize()));
        pluginConfig.setUseLastPageIfPageNoOut(accessor.getBoolean(paginationPluginConfigPrefix + "useLastPageIfPageNoOut", pluginConfig.isUseLastPageIfPageNoOut()));

        String instrumentorConfigPrefix = "sqlhelper.mybatis.instrumentor.";
        instrumentConfig.setDialect(accessor.getString(instrumentorConfigPrefix + "dialect", instrumentConfig.getDialect()));
        instrumentConfig.setDialectClassName(accessor.getString(instrumentorConfigPrefix + "dialectClassName", instrumentConfig.getDialectClassName()));
        instrumentConfig.setCacheInstrumentedSql(accessor.getBoolean(instrumentorConfigPrefix + "cacheInstruemtedSql", false));
    }

    public void setPaginationPluginConfig(PaginationPluginConfig config) {
        this.pluginConfig = config;
    }

    private boolean isUseLastPageIfPageNoOut(@NonNull PagingRequest request) {
        Preconditions.checkNotNull(request);
        if (request.isUseLastPageIfPageOut() == null) {
            return pluginConfig.isUseLastPageIfPageNoOut();
        }
        return request.isUseLastPageIfPageOut();
    }

    public Object intercept(final Invocation invocation) {
        if (logger.isDebugEnabled()) {
            logger.debug("{}", invocation);
        }
        final Object[] args = invocation.getArgs();
        final MappedStatement ms = (MappedStatement) args[0];
        final Object parameter = args[1];
        final RowBounds rowBounds = (RowBounds) args[2];
        final Executor executor = (Executor) invocation.getTarget();
        BoundSql boundSql = null;
        CacheKey cacheKey = null;
        ResultHandler resultHandler = null;
        if (args.length >= NON_CACHE_QUERY_METHOD_PARAMS) {
            resultHandler = (ResultHandler) args[3];
            if (args.length == NON_CACHE_QUERY_METHOD_PARAMS) {
                boundSql = ms.getBoundSql(parameter);
                cacheKey = executor.createCacheKey(ms, parameter, rowBounds, boundSql);
            } else {
                cacheKey = (CacheKey) args[4];
                boundSql = (BoundSql) args[5];
            }
        }

        Object rs = null;

        setPagingRequestBasedRowBounds(rowBounds);

        try {
            if (!isPagingRequest(ms)) {
                if (MybatisUtils.isQueryStatement(ms) && PAGING_CONTEXT.isPagingRequest() && PAGING_CONTEXT.isOrderByRequest() && !isNestedQueryInPagingRequest(ms)) {
                    // do order by
                    rs = executeOrderBy(PAGING_CONTEXT.getPagingRequest().getOrderBy(), ms, parameter, RowBounds.DEFAULT, resultHandler, executor, boundSql);
                } else {
                    // not a paging request, not a order by paging request
                    rs = invocation.proceed();
                }
                if (rs == null) {
                    rs = Collects.emptyArrayList();
                }
                invalidatePagingRequest(false);
            } else if (isNestedQueryInPagingRequest(ms)) {
                rs = invocation.proceed();
                if (rs == null) {
                    rs = Collects.emptyArrayList();
                }
            } else {
                final PagingRequest request = PAGING_CONTEXT.getPagingRequest();
                final PagingResult result = new PagingResult();
                request.setResult(result);
                result.setPageSize(request.getPageSize());
                List items = new ArrayList();
                int requestPageNo = request.getPageNo();
                result.setPageNo(request.getPageNo());
                result.setItems(items);

                if (request.isEmptyRequest()) {
                    result.setTotal(0);
                    rs = items;
                    return rs;
                }
                if (request.isGetAllRequest()) {
                    if (PAGING_CONTEXT.isOrderByRequest()) {
                        rs = executeOrderBy(PAGING_CONTEXT.getPagingRequest().getOrderBy(), ms, parameter, RowBounds.DEFAULT, resultHandler, executor, boundSql);
                    } else {
                        rs = invocation.proceed();
                    }
                    if (rs == null) {
                        rs = Collects.emptyArrayList();
                    }
                    if (rs instanceof Collection) {
                        items.addAll((Collection) rs);
                        result.setTotal(items.size());
                    }
                    invalidatePagingRequest(false);
                    return rs;
                }

                if (this.beginIfSupportsLimit(ms)) {
                    boolean needQuery = true;
                    try {
                        if (this.needCount(request)) {
                            final int count = this.executeCount(ms, parameter, rowBounds, resultHandler, executor, boundSql);
                            if (count == 0) {
                                needQuery = false;
                            }
                            result.setTotal(count);
                            int maxPageCount = result.getMaxPage();
                            if (maxPageCount >= 0) {
                                if (requestPageNo > maxPageCount) {
                                    if (isUseLastPageIfPageNoOut(request)) {
                                        request.setPageNo(maxPageCount);
                                        result.setPageNo(maxPageCount);
                                    } else {
                                        needQuery = false;
                                    }
                                }
                            }
                        } else {
                            result.setTotal(-1);
                        }
                    } catch (Throwable ex) {
                        logger.error(ex.getMessage(), ex);
                    } finally {
                        if (needQuery) {
                            List rows = this.executeQuery(ms, parameter, rowBounds, resultHandler, executor, boundSql, cacheKey);
                            if (rows != null) {
                                items.addAll(rows);
                            }
                        }
                    }
                    request.setPageNo(requestPageNo);
                    result.setPageNo(request.getPageNo());
                    rs = items;
                } else {
                    rs = invocation.proceed();
                    if (rs == null) {
                        rs = Collects.emptyArrayList();
                    }
                }
            }
        } catch (Throwable ex2) {
            logger.error(ex2.getMessage(), ex2);
            throw Throwables.wrapAsRuntimeException(ex2);
        } finally {
            invalidatePagingRequest(false);
            SQLStatementInstrumentor instrumentor = SqlHelperMybatisPluginContext.getInstance().getInstrumentor();
            instrumentor.finish();
        }
        return rs;
    }

    private void setPagingRequestBasedRowBounds(RowBounds rowBounds) {
        if (MybatisUtils.isPagingRowBounds(rowBounds)) {
            PagingRequest request = new PagingRequest();
            request.setPageSize(rowBounds.getLimit());
            request.setPageNo(rowBounds.getOffset() / rowBounds.getLimit() + rowBounds.getOffset() % rowBounds.getLimit() == 0 ? 0 : 1);
            PagingRequestContextHolder.getContext().setPagingRequest(request);
        }
    }


    private void invalidatePagingRequest(boolean force) {
        PagingRequest request = PAGING_CONTEXT.getPagingRequest();
        if (request != null) {
            request.clear(force);
        }
        PAGING_CONTEXT.remove();
    }

    private boolean isPagingRequest(final MappedStatement statement) {
        return MybatisUtils.isPreparedStatement(statement) && MybatisUtils.isQueryStatement(statement) && PAGING_CONTEXT.isPagingRequest();
    }

    private boolean isNestedQueryInPagingRequest(final MappedStatement statement) {
        if (PAGING_CONTEXT.get().getString(MybatisPaginationRequestContextKeys.QUERY_SQL_ID) != null) {
            if (!PAGING_CONTEXT.get().getString(MybatisPaginationRequestContextKeys.QUERY_SQL_ID).equals(statement.getId())) {
                // the statement is a nested statement
                return true;
            }
        }
        return false;
    }

    private boolean beginIfSupportsLimit(final MappedStatement statement) {
        if (!PAGING_CONTEXT.getPagingRequest().isValidRequest()) {
            invalidatePagingRequest(false);
            return false;
        }
        if (PAGING_CONTEXT.get().getString(MybatisPaginationRequestContextKeys.QUERY_SQL_ID) != null) {
            if (isNestedQueryInPagingRequest(statement)) {
                return false;
            }
        } else {
            PAGING_CONTEXT.get().setString(MybatisPaginationRequestContextKeys.QUERY_SQL_ID, statement.getId());
        }
        SQLStatementInstrumentor instrumentor = SqlHelperMybatisPluginContext.getInstance().getInstrumentor();
        final String databaseId = MybatisUtils.getDatabaseId(PAGING_CONTEXT, instrumentor, statement);
        return instrumentor.beginIfSupportsLimit(databaseId);
    }


    private List executeQuery(final MappedStatement ms, final Object parameter, final RowBounds rowBounds, final ResultHandler resultHandler, final Executor executor, final BoundSql boundSql, final CacheKey cacheKey) throws SQLException {
        final PagingRequest request = PAGING_CONTEXT.getPagingRequest();
        final RowSelection rowSelection = rowSelectionBuilder.build(request);
        PAGING_CONTEXT.setRowSelection(rowSelection);

        String pageSql = boundSql.getSql();

        boolean subQueryPagination = false;
        SQLStatementInstrumentor instrumentor = SqlHelperMybatisPluginContext.getInstance().getInstrumentor();
        if (SqlPaginations.isSubqueryPagingRequest(request)) {
            if (!SqlPaginations.isValidSubQueryPagination(request, instrumentor)) {
                logger.warn("Paging request is not a valid subquery pagination request, so the paging request will not as a subquery pagination request. request: {}, the instrument configuration is: {}", request, instrumentor.getConfig());
            } else {
                subQueryPagination = true;
            }
        }
        if (!subQueryPagination) {
            if (PAGING_CONTEXT.isOrderByRequest()) {
                pageSql = instrumentor.instrumentOrderByLimitSql(boundSql.getSql(), PAGING_CONTEXT.getPagingRequest().getOrderBy(), rowSelection);
            } else {
                pageSql = instrumentor.instrumentLimitSql(boundSql.getSql(), rowSelection);
            }
            PagingRequestContext ctx = PAGING_CONTEXT.get();
            ctx.setInteger(PagingRequestContext.BEFORE_SUBQUERY_PARAMETERS_COUNT, 0);
            ctx.setInteger(PagingRequestContext.AFTER_SUBQUERY_PARAMETERS_COUNT, 0);
        } else {
            String startFlag = SqlPaginations.getSubqueryPaginationStartFlag(request, instrumentor);
            String endFlag = SqlPaginations.getSubqueryPaginationEndFlag(request, instrumentor);
            String subqueryPartition = SqlPaginations.extractSubqueryPartition(boundSql.getSql(), startFlag, endFlag);
            String limitedSubqueryPartition = instrumentor.instrumentLimitSql(subqueryPartition, rowSelection);
            String beforeSubqueryPartition = SqlPaginations.extractBeforeSubqueryPartition(boundSql.getSql(), startFlag);
            String afterSubqueryPartition = SqlPaginations.extractAfterSubqueryPartition(boundSql.getSql(), endFlag);
            pageSql = beforeSubqueryPartition + " " + limitedSubqueryPartition + " " + afterSubqueryPartition;
            if (PAGING_CONTEXT.isOrderByRequest()) {
                pageSql = instrumentor.instrumentOrderBySql(pageSql, PAGING_CONTEXT.getPagingRequest().getOrderBy());
            }

            PagingRequestContext ctx = PAGING_CONTEXT.get();
            ctx.setInteger(PagingRequestContext.BEFORE_SUBQUERY_PARAMETERS_COUNT, SqlPaginations.findPlaceholderParameterCount(beforeSubqueryPartition));
            ctx.setInteger(PagingRequestContext.AFTER_SUBQUERY_PARAMETERS_COUNT, SqlPaginations.findPlaceholderParameterCount(afterSubqueryPartition));
        }

        final BoundSql pageBoundSql = MybatisUtils.rebuildBoundSql(pageSql, ms.getConfiguration(), boundSql);
        cacheKey.update(request.getPageNo());
        cacheKey.update(request.getPageSize());
        return executor.query(ms, parameter, RowBounds.DEFAULT, resultHandler, cacheKey, pageBoundSql);
    }

    private String getOrderById(final MappedStatement ms, final OrderBy orderBy) {
        String orderByString = orderBy.toString();
        StringBuilder builder = new StringBuilder(ms.getId() + "_");
        for (int i = 0; i < orderByString.length(); i++) {
            char c = orderByString.charAt(i);
            // 0-9, a-z, _
            if (Chars.isNumber(c) || Chars.isLowerCase(c) || Chars.isUpperCase(c) || c == '_') {
                builder.append(c);
            }
        }
        return builder.append(ORDER_BY_SUFFIX).toString();
    }

    private MappedStatement customOrderByStatement(final MappedStatement ms, final String orderByStatementId) {
        MappedStatement orderBySqlStatement = null;
        final MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), orderByStatementId, ms.getSqlSource(), ms.getSqlCommandType());
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
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        orderBySqlStatement = builder.build();
        return orderBySqlStatement;
    }

    private Object executeOrderBy(OrderBy orderBy, final MappedStatement ms, final Object parameter, final RowBounds rowBounds, final ResultHandler resultHandler, final Executor executor, final BoundSql boundSql) throws Throwable {
        SQLStatementInstrumentor instrumentor = SqlHelperMybatisPluginContext.getInstance().getInstrumentor();
        String orderBySqlId = getOrderById(ms, orderBy);
        BoundSql orderByBoundSql = null;
        MappedStatement orderByStatement = this.customOrderByStatement(ms, orderBySqlId);
        final Map<String, Object> additionalParameters = MybatisPaginationPlugin.BoundSqls.getAdditionalParameter(boundSql);
        final CacheKey orderByCacheKey = executor.createCacheKey(orderByStatement, parameter, RowBounds.DEFAULT, boundSql);
        final String orderBySql = instrumentor.instrumentOrderBySql(boundSql.getSql(), orderBy);
        orderByBoundSql = MybatisUtils.rebuildBoundSql(orderBySql, orderByStatement.getConfiguration(), boundSql);
        return executor.query(orderByStatement, parameter, RowBounds.DEFAULT, resultHandler, orderByCacheKey, orderByBoundSql);
    }


    private int executeCount(final MappedStatement ms, final Object parameter, final RowBounds rowBounds, final ResultHandler resultHandler, final Executor executor, final BoundSql boundSql) throws Throwable {
        final PagingRequestContext requestContext = PAGING_CONTEXT.get();
        final PagingRequest request = PAGING_CONTEXT.getPagingRequest();
        final String countStatementId = this.getCountStatementId(request, ms.getId());
        int count;
        BoundSql countBoundSql = null;
        try {
            MappedStatement countStatement = this.extractCountStatementFromConfiguration(ms.getConfiguration(), countStatementId);
            if (countStatement != null) {
                final CacheKey countKey = executor.createCacheKey(countStatement, parameter, RowBounds.DEFAULT, boundSql);
                countKey.update(request.getPageNo());
                countKey.update(request.getPageSize());
                countBoundSql = countStatement.getBoundSql(parameter);
                requestContext.set(MybatisPaginationRequestContextKeys.COUNT_SQL, countBoundSql);
                final Object countResultList = executor.query(countStatement, parameter, RowBounds.DEFAULT, resultHandler, countKey, countBoundSql);
                count = ((Number) ((List) countResultList).get(0)).intValue();
            } else {
                String querySql = boundSql.getSql();
                SQLStatementInstrumentor instrumentor = SqlHelperMybatisPluginContext.getInstance().getInstrumentor();
                final String countSql = instrumentor.countSql(querySql, request.getCountColumn());
                countStatement = this.customCountStatement(ms, countStatementId, querySql);

                final Map<String, Object> additionalParameters = MybatisPaginationPlugin.BoundSqls.getAdditionalParameter(boundSql);
                final CacheKey countKey2 = executor.createCacheKey(countStatement, parameter, RowBounds.DEFAULT, boundSql);
                countKey2.update(request.getPageNo());
                countKey2.update(request.getPageSize());

                countBoundSql = MybatisUtils.rebuildBoundSql(countSql, countStatement.getConfiguration(), boundSql);
                requestContext.set(MybatisPaginationRequestContextKeys.COUNT_SQL, countBoundSql);
                final Object countResultList2 = executor.query(countStatement, parameter, RowBounds.DEFAULT, resultHandler, countKey2, countBoundSql);
                count = ((Number) ((List) countResultList2).get(0)).intValue();
            }
        } catch (Throwable ex) {
            if (countBoundSql != null) {
                logger.error("error occur when execute count sql [{}], error: {}", countBoundSql.getSql(), ex.getMessage(), ex);
            }
            throw ex;
        } finally {
            requestContext.set(MybatisPaginationRequestContextKeys.COUNT_SQL, null);
        }
        return count;
    }

    private boolean needCount(final PagingRequest request) {
        if (request.needCount() == null) {
            return pluginConfig.isCount();
        }
        if (Boolean.TRUE.compareTo(request.needCount()) == 0) {
            return !SqlPaginations.isSubqueryPagingRequest(request);
        }
        return false;
    }

    private String getCountStatementId(final PagingRequest request, final String currentSqlId) {
        String customCountSqlId = PAGING_CONTEXT.get().getString(MybatisPaginationRequestContextKeys.COUNT_SQL_ID);
        if (!Strings.isBlank(customCountSqlId)) {
            return customCountSqlId;
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

    private MappedStatement customCountStatement(final MappedStatement ms, final String countStatementId, String querySql) {
        MappedStatement countStatement = pluginConfig.enableCountCache() ? this.countStatementCache.getIfPresent(querySql) : null;
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
            final List<ResultMap> resultMaps = new ArrayList<ResultMap>();
            final ResultMap resultMap = new ResultMap.Builder(ms.getConfiguration(), ms.getId(), Long.class, new ArrayList<ResultMapping>()).build();
            resultMaps.add(resultMap);
            builder.resultMaps(resultMaps);
            builder.resultSetType(ms.getResultSetType());
            builder.cache(ms.getCache());
            builder.flushCacheRequired(ms.isFlushCacheRequired());
            builder.useCache(ms.isUseCache());

            countStatement = builder.build();
            if (pluginConfig.enableCountCache() && ms.isUseCache()) {
                this.countStatementCache.set(querySql, countStatement);
            }
        }
        return countStatement;
    }

}
