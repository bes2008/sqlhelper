
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

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.cache.Cache;
import com.jn.langx.cache.CacheBuilder;
import com.jn.langx.cache.Loader;
import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.sqlhelper.dialect.Dialect;
import com.jn.sqlhelper.dialect.DialectRegistry;
import com.jn.sqlhelper.dialect.SQLDialectException;
import com.jn.sqlhelper.dialect.expression.SQLExpression;
import com.jn.sqlhelper.dialect.expression.builder.SQLSymbolExpressionBuilderRegistry;
import com.jn.sqlhelper.dialect.expression.columnevaluation.BuiltinColumnEvaluationExpressionSupplier;
import com.jn.sqlhelper.dialect.expression.columnevaluation.ColumnEvaluationExpressionSupplier;
import com.jn.sqlhelper.dialect.instrument.orderby.DefaultOrderByTransformer;
import com.jn.sqlhelper.dialect.instrument.orderby.OrderByTransformer;
import com.jn.sqlhelper.dialect.instrument.where.WhereTransformConfig;
import com.jn.sqlhelper.dialect.internal.limit.LimitHelper;
import com.jn.sqlhelper.dialect.orderby.OrderBy;
import com.jn.sqlhelper.dialect.pagination.PagedPreparedParameterSetter;
import com.jn.sqlhelper.dialect.pagination.QueryParameters;
import com.jn.sqlhelper.dialect.pagination.RowSelection;
import com.jn.sqlhelper.dialect.sqlparser.SqlStatementWrapper;
import com.jn.sqlhelper.dialect.sqlparser.StringSqlStatementWrapper;
import com.jn.sqlhelper.dialect.tenant.Tenant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLStatementInstrumentor implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(SQLStatementInstrumentor.class);
    private static final ThreadLocal<Dialect> DIALECT_HOLDER = new ThreadLocal<Dialect>();
    private final static List<String> keywordsNotAfterOrderBy = Collects.asList("select", "?", "union", "from", "where", "and", "or", "between", "in", "case");
    @NonNull
    private SQLInstrumentorConfig config;
    private DialectRegistry dialectRegistry;
    private volatile boolean inited = false;
    private String name;
    private Instrumentation instrumentation;
    private SQLSymbolExpressionBuilderRegistry sqlSymbolExpressionBuilderRegistry = new SQLSymbolExpressionBuilderRegistry();
    private ColumnEvaluationExpressionSupplier columnEvaluationExpressionSupplier;
    /**
     * order by transformer proxy
     */
    private OrderByTransformer orderByTransformer;
    private Cache<String, InstrumentedStatement> instrumentSqlCache;

    public SQLStatementInstrumentor() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void init() throws InitializationException {
        if (!inited) {
            if (this.config == null) {
                throw new IllegalStateException("the 'config' field is null");
            }
            setName(this.config.getName());
            logger.info("Start to initial the {} SQLStatementInstrumentor with configuration{}", this.name, this.config);
            this.dialectRegistry = DialectRegistry.getInstance();
            inited = true;
            if (this.config.isCacheInstrumentedSql()) {
                instrumentSqlCache = CacheBuilder.<String, InstrumentedStatement>newBuilder()
                        .initialCapacity(config.getCacheInitialCapacity())
                        .maxCapacity(config.getCacheMaxCapacity())
                        .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                        .expireAfterRead(config.getCacheExpireAfterRead())
                        .loader(new Loader<String, InstrumentedStatement>() {
                            @Override
                            public InstrumentedStatement load(String originalSql) {
                                InstrumentedStatement s = new InstrumentedStatement();
                                s.setOriginalSql(originalSql);
                                return s;
                            }

                            @Override
                            public Map<String, InstrumentedStatement> getAll(Iterable<String> keys) {
                                final Map<String, InstrumentedStatement> map = new HashMap<String, InstrumentedStatement>();
                                Collects.forEach(keys, new Consumer<String>() {
                                    @Override
                                    public void accept(String k) {
                                        map.put(k, load(k));
                                    }
                                });
                                return map;
                            }
                        })
                        .build();
            }
            InstrumentationRegistry.getInstance().enableInstrumentation(this.config.getInstrumentation());
            this.instrumentation = InstrumentationRegistry.getInstance().findInstrumentation(this.config.getInstrumentation());
            Preconditions.checkNotNull(instrumentation, "Can't find a suitable or enabled SQL instrumentation, please add the sqlhelper-jsqlparser.jar to your classpath");
            orderByTransformer = new DefaultOrderByTransformer();
            orderByTransformer.setInstrumentation(instrumentation);
            orderByTransformer.init();

            sqlSymbolExpressionBuilderRegistry.init();
            if (columnEvaluationExpressionSupplier == null) {
                columnEvaluationExpressionSupplier = new BuiltinColumnEvaluationExpressionSupplier();
            }

            columnEvaluationExpressionSupplier.setExpressionBuilderRegistry(sqlSymbolExpressionBuilderRegistry);

            logger.info("The {} SQLStatementInstrumentor initial finish", this.name);
        }
    }

    public boolean beginIfSupportsLimit(final Statement statement) {
        final Dialect dialect = this.getDialect(statement);
        return this.beginIfSupportsLimit(dialect);
    }

    public boolean beginIfSupportsLimit(final String databaseId) {
        final Dialect dialect = this.dialectRegistry.getDialectByName(databaseId);
        return this.beginIfSupportsLimit(dialect);
    }

    public boolean beginIfSupportsLimit(final DatabaseMetaData databaseMetaData) {
        final Dialect dialect = this.getDialect(databaseMetaData);
        return this.beginIfSupportsLimit(dialect);
    }

    private boolean beginIfSupportsLimit(final Dialect dialect) {
        if (dialect == null) {
            return false;
        }
        final boolean supports = dialect.isSupportsLimit();
        if (supports) {
            SQLStatementInstrumentor.DIALECT_HOLDER.set(dialect);
        }
        return supports;
    }

    public Dialect getCurrentDialect() {
        return SQLStatementInstrumentor.DIALECT_HOLDER.get();
    }

    private Dialect getDialect(final Statement statement) {
        Dialect dialect = null;
        if (statement != null) {
            try {
                final Connection connection = statement.getConnection();
                dialect = getDialect(connection.getMetaData());
            } catch (SQLException e) {
                logger.error("sql error code: {}, message: {}", e.getErrorCode(), e.getMessage(), e);
            }
        }
        return dialect;
    }

    public Dialect getDialect(@Nullable DatabaseMetaData databaseMetaData) {
        Dialect dialect = this.getCurrentDialect();
        if (dialect != null) {
            return dialect;
        }
        final String dialectName = this.config.getDialect();
        if (dialectName != null) {
            dialect = this.dialectRegistry.getDialectByName(dialectName);
        }
        if (dialect == null && this.config.getDialectClassName() != null) {
            dialect = this.dialectRegistry.getDialectByClassName(this.config.getDialectClassName());
        }
        if (dialect == null && databaseMetaData != null) {
            dialect = this.dialectRegistry.getDialectByDatabaseMetadata(databaseMetaData);
        }
        return dialect;
    }

    public String instrumentLimitSql(String sql, final RowSelection selection) {
        final Dialect dialect = this.getCurrentDialect();
        return instrumentLimitSql(dialect, sql, selection);
    }

    public String instrumentLimitSql(Dialect dialect, String sql, final RowSelection selection) {
        if (LimitHelper.useLimit(dialect, selection) && dialect.isSupportsVariableLimit()) {
            String originalSql = sql;
            if (this.config.isCacheInstrumentedSql()) {
                sql = getInstrumentedStatement(originalSql).getLimitSql(dialect.getDatabaseId(), selection.hasOffset());
                if (sql != null) {
                    return sql;
                }
            }
            sql = dialect.getLimitSql(originalSql, selection);
            if (this.config.isCacheInstrumentedSql()) {
                getInstrumentedStatement(originalSql).setLimitSql(dialect.getDatabaseId(), sql, selection.hasOffset());
            }
        }
        return sql;
    }

    public String instrumentOrderBySql(String sql, OrderBy orderBy) {
        if (this.config.isCacheInstrumentedSql()) {
            String orderBySql = getInstrumentedStatement(sql).getOrderBySql(orderBy);
            if (orderBySql != null) {
                return orderBySql;
            }
        }
        try {
            SqlStatementWrapper sqlStatementWrapper = parseSql(sql);
            TransformConfig transformConfig = new TransformConfig();
            transformConfig.setOrderBy(orderBy);
            orderByTransformer.transform(sqlStatementWrapper, transformConfig);
            String sql2 = sqlStatementWrapper.getSql();
            if (sql2 != null) {
                if (this.config.isCacheInstrumentedSql()) {
                    getInstrumentedStatement(sql).setOrderBySql(orderBy, sql2);
                }
                return sql2;
            }
        } catch (Throwable ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return sql;
    }

    private SqlStatementWrapper parseSql(String sql) {
        try {
            return instrumentation.getSqlParser().parse(sql);
        } catch (Throwable ex) {
            logger.error("error occur when parse the sql with jsqlparser: {}", sql);
        }

        StringSqlStatementWrapper sqlStatementWrapper = new StringSqlStatementWrapper();
        sqlStatementWrapper.setOriginalSql(sql);
        sqlStatementWrapper.setStatement(sql);
        return sqlStatementWrapper;
    }

    public String instrumentOrderByLimitSql(String sql, OrderBy orderBy, final RowSelection selection) {
        final Dialect dialect = this.getCurrentDialect();
        return instrumentOrderByLimitSql(sql, orderBy, dialect, selection);
    }

    public String instrumentOrderByLimitSql(String sql, OrderBy orderBy, Dialect dialect, final RowSelection selection) {
        String originalSql = sql;
        if (orderBy == null) {
            throw new IllegalArgumentException("Illegal argument : orderBy");
        }
        sql = instrumentLimitSql(dialect, sql, selection);
        sql = instrumentOrderBySql(sql, orderBy);
        if (this.config.isCacheInstrumentedSql()) {
            getInstrumentedStatement(originalSql).setOrderByLimitSql(orderBy, dialect.getDatabaseId(), sql, selection.hasOffset());
        }
        return sql;
    }

    public String instrumentTenantSql(String sql, Tenant tenant) {
        if (tenant == null) {
            return sql;
        }


        try {
            WhereTransformConfig whereTransformConfig = new WhereTransformConfig();
            whereTransformConfig.setInstrumentSubSelect(false);
            whereTransformConfig.setPosition(InjectPosition.FIRST);
            SQLExpression sqlExpression = columnEvaluationExpressionSupplier.get(tenant);
            whereTransformConfig.setExpression(sqlExpression);

            TransformConfig transformConfig = new TransformConfig();
            transformConfig.setWhereInstrumentConfigs(Collects.asList(whereTransformConfig));

            if (this.config.isCacheInstrumentedSql()) {
                String tenantSql = getInstrumentedStatement(sql).getInstrumentedSql(transformConfig);
                if (tenantSql != null) {
                    return tenantSql;
                }
            }

            SqlStatementWrapper statementWrapper = parseSql(sql);
            instrumentation.getWhereTransformer().transform(statementWrapper, transformConfig);
            String newSql = statementWrapper.getSql();
            if (newSql != null) {
                if (this.config.isCacheInstrumentedSql()) {
                    getInstrumentedStatement(sql).setInstrumentedSql(transformConfig, newSql);
                }
                return newSql;
            }
        } catch (Throwable ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return sql;
    }

    public void finish() {
        DIALECT_HOLDER.remove();
    }

    public String countSql(String originalSql) {
        return countSql(originalSql, null);
    }

    public String countSql(String originalSql, String countColumn) {
        if (Strings.isBlank(countColumn)) {
            countColumn = "1";
        }
        InstrumentedStatement instrumentedSql = getInstrumentedStatement(originalSql);
        if (instrumentedSql != null) {
            String countSql = instrumentedSql.getCountSql();
            if (countSql != null) {
                return countSql;
            }
        }

        // do count
        boolean sliceOrderBy = false;
        final String lowerSql = originalSql.toLowerCase();
        final int orderIndex = lowerSql.lastIndexOf("order");
        if (orderIndex != -1) {
            String remainSql = lowerSql.substring(orderIndex + "order".length()).trim();
            sliceOrderBy = remainSql.startsWith("by");
            if (sliceOrderBy) {
                remainSql = Strings.replace(remainSql, "(", " ( ");
                remainSql = Strings.replace(remainSql, ")", " ) ");
                Pipeline<String> pipeline = Pipeline.<String>of(remainSql.split("[\\s,]+")).filter(new Predicate<String>() {
                    @Override
                    public boolean test(String value) {
                        return Strings.isNotEmpty(value);
                    }
                });
                if (pipeline.anyMatch(new Predicate<String>() {
                    @Override
                    public boolean test(String value) {
                        return keywordsNotAfterOrderBy.contains(value);
                    }
                })) {
                    sliceOrderBy = false;
                }
                if (sliceOrderBy) {
                    int leftBracketsCount = 0;
                    List<String> list = pipeline.asList();
                    for (int i = 0; i < list.size(); i++) {
                        String c = list.get(i);
                        if (c.equals("(")) {
                            leftBracketsCount++;
                        } else if (c.equals(")")) {
                            leftBracketsCount--;
                            if (leftBracketsCount < 0) {
                                sliceOrderBy = false;
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (sliceOrderBy) {
            originalSql = originalSql.substring(0, orderIndex).trim();
        }
        String countSql = "select count(" + countColumn + ") from (" + originalSql + ") tmp_count";

        // cache it
        if (this.config.isCacheInstrumentedSql()) {
            getInstrumentedStatement(originalSql).setCountSql(countSql);
        }
        return countSql;
    }


    private InstrumentedStatement getInstrumentedStatement(String originalSql) {
        if (this.config.isCacheInstrumentedSql()) {
            try {
                return this.instrumentSqlCache.get(originalSql);
            } catch (Throwable e) {
                // ignore it
            }
        }
        return null;
    }

    private InstrumentedStatement getInstrumentedStatementIfPresent(String originalSql) {
        if (this.config.isCacheInstrumentedSql()) {
            return this.instrumentSqlCache.getIfPresent(originalSql);
        }
        return null;
    }

    /**
     * bind all parameters for a subquery pagination sql
     *
     * @param statement       the sql statement
     * @param queryParameters all the original parameters
     * @return the count of set in the invocation
     * @throws SQLException        throw it if error
     * @throws SQLDialectException
     */
    public PreparedStatement bindParameters(final PreparedStatement statement, final PagedPreparedParameterSetter parameterSetter, final QueryParameters queryParameters, final boolean setOriginalParameters) throws SQLException, SQLDialectException {
        final Dialect dialect = this.getDialect(statement);
        return bindParameters(dialect, statement, parameterSetter, queryParameters, setOriginalParameters);
    }

    /**
     * bind all parameters for a subquery pagination sql
     *
     * @param statement       the sql statement
     * @param queryParameters all the original parameters
     * @return the count of set in the invocation
     * @throws SQLException        throw it if error
     * @throws SQLDialectException
     */
    public PreparedStatement bindParameters(Dialect dialect, final PreparedStatement statement, final PagedPreparedParameterSetter parameterSetter, final QueryParameters queryParameters, final boolean setOriginalParameters) throws SQLException, SQLDialectException {
        final RowSelection selection = queryParameters.getRowSelection();
        final boolean callable = queryParameters.isCallable();
        try {
            int col = 1;
            int countOfBeforeSubquery = queryParameters.getBeforeSubqueryParameterCount();
            int countOfAfterSubquery = queryParameters.getAfterSubqueryParameterCount();
            if (setOriginalParameters && countOfBeforeSubquery > 0) {
                col += parameterSetter.setBeforeSubqueryParameters(statement, queryParameters, col);
            }
            col += dialect.bindLimitParametersAtStartOfQuery(selection, statement, col);
            if (callable) {
                col = dialect.registerResultSetOutParameter((CallableStatement) statement, col);
            }
            if (setOriginalParameters) {
                if (countOfBeforeSubquery < 1 && countOfAfterSubquery < 1) {
                    col += parameterSetter.setOriginalParameters(statement, queryParameters, col);
                } else {
                    col += parameterSetter.setSubqueryParameters(statement, queryParameters, col);
                }
            }
            col += dialect.bindLimitParametersAtEndOfQuery(selection, statement, col);
            if (setOriginalParameters && countOfAfterSubquery > 0) {
                col += parameterSetter.setAfterSubqueryParameters(statement, queryParameters, col);
            }
            dialect.setMaxRows(selection, statement);
            if (selection != null) {
                if (selection.getTimeout() != null && selection.getTimeout() > 0) {
                    statement.setQueryTimeout(selection.getTimeout());
                }
                if (selection.getFetchSize() != null && selection.getFetchSize() > 0) {
                    statement.setFetchSize(selection.getFetchSize());
                }
            }
        } catch (SQLException ex) {
            logger.error("Set sql parameter fail, errorCode: {}, stack:{}", ex.getErrorCode(), ex);
        }
        return statement;
    }

    public SQLInstrumentorConfig getConfig() {
        return this.config;
    }


    @NonNull
    public void setConfig(final SQLInstrumentorConfig config) {
        if (!inited) {
            this.config = config;
        }
    }

    public DialectRegistry getDialectRegistry() {
        return this.dialectRegistry;
    }

    public void setDialectRegistry(final DialectRegistry dialectRegistry) {
        this.dialectRegistry = dialectRegistry;
    }


}
