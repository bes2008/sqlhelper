
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

package com.jn.sqlhelper.dialect;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.cache.Cache;
import com.jn.langx.cache.CacheBuilder;
import com.jn.langx.cache.Loader;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.sqlhelper.dialect.conf.SQLInstrumentConfig;
import com.jn.sqlhelper.dialect.internal.limit.LimitHelper;
import com.jn.sqlhelper.dialect.orderby.OrderBy;
import com.jn.sqlhelper.dialect.orderby.OrderByInstrumentor;
import com.jn.sqlhelper.dialect.pagination.PagedPreparedParameterSetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLStatementInstrumentor {
    private static final Logger logger = LoggerFactory.getLogger(SQLStatementInstrumentor.class);
    @NonNull
    private SQLInstrumentConfig config;
    private DialectRegistry dialectRegistry;
    private static final ThreadLocal<Dialect> DIALECT_HOLDER = new ThreadLocal<Dialect>();
    private boolean inited = false;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private Cache<String, InstrumentedSelectStatement> instrumentSqlCache;

    public SQLStatementInstrumentor() {

    }

    public void init() {
        if (!inited) {
            if (this.config == null) {
                throw new IllegalStateException("the 'config' field is null");
            }
            setName(this.config.getName());
            logger.info("Start to initial the {} SQLStatementInstrumentor with configuration{}", this.name, this.config);
            this.dialectRegistry = DialectRegistry.getInstance();
            inited = true;
            if (this.config.isCacheInstrumentedSql()) {
                instrumentSqlCache = CacheBuilder.<String, InstrumentedSelectStatement>newBuilder()
                        .initialCapacity(1000)
                        .maxCapacity(Integer.MAX_VALUE)
                        .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                        .expireAfterRead(5 * 60)
                        .loader(new Loader<String, InstrumentedSelectStatement>() {
                            @Override
                            public InstrumentedSelectStatement load(String originalSql) {
                                InstrumentedSelectStatement s = new InstrumentedSelectStatement();
                                s.setOriginalSql(originalSql);
                                return s;
                            }

                            @Override
                            public Map<String, InstrumentedSelectStatement> getAll(Iterable<String> keys) {
                                final Map<String, InstrumentedSelectStatement> map = new HashMap<String, InstrumentedSelectStatement>();
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

    private Dialect getDialect(@Nullable DatabaseMetaData databaseMetaData) {
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
                sql = getInstrumentedSelectStatement(originalSql).getLimitSql(dialect.getDatabaseId(), selection.hasOffset());
                if (sql != null) {
                    return sql;
                }
            }
            sql = dialect.getLimitSql(originalSql, selection);
            if (this.config.isCacheInstrumentedSql()) {
                getInstrumentedSelectStatement(originalSql).setLimitSql(dialect.getDatabaseId(), sql, selection.hasOffset());
            }
        }
        return sql;
    }

    public String instrumentOrderBySql(String sql, OrderBy orderBy) {
        if (this.config.isCacheInstrumentedSql()) {
            String orderBySql = getInstrumentedSelectStatement(sql).getOrderBySql(orderBy);
            if (orderBySql != null) {
                return orderBySql;
            }
        }
        try {
            String sql2 = OrderByInstrumentor.instrument(sql, orderBy);
            if (sql2 != null) {
                if (this.config.isCacheInstrumentedSql()) {
                    getInstrumentedSelectStatement(sql).setOrderBySql(orderBy, sql2);
                }
                return sql2;
            }
        } catch (Throwable ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return sql;
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
            getInstrumentedSelectStatement(originalSql).setOrderByLimitSql(orderBy, dialect.getDatabaseId(), sql, selection.hasOffset());
        }
        return sql;
    }

    public void finish() {
        DIALECT_HOLDER.remove();
    }

    public String countSql(String originalSql) {
        return countSql(originalSql, null);
    }

    private final static List<String> keywordsNotAfterOrderBy = Collects.asList("select", "?", "union", "from", "where", "and", "or", "between", "in", "case");

    public String countSql(String originalSql, String countColumn) {
        if (Strings.isBlank(countColumn)) {
            countColumn = "1";
        }
        InstrumentedSelectStatement instrumentedSql = getInstrumentedSelectStatement(originalSql);
        if (instrumentedSql != null) {
            String countSql = instrumentedSql.getCountSql();
            if (countSql != null) {
                return countSql;
            }
        }

        // do count
        boolean sliceOrderBy = false;
        final String lowerSql = originalSql.toLowerCase().trim();
        final int orderIndex = originalSql.toLowerCase().lastIndexOf("order");
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
            originalSql = originalSql.trim().substring(0, orderIndex).trim();
        }
        String countSql = "select count(" + countColumn + ") from (" + originalSql + ") tmp_count";

        // cache it
        if (this.config.isCacheInstrumentedSql()) {
            getInstrumentedSelectStatement(originalSql).setCountSql(countSql);
        }
        return countSql;
    }


    private InstrumentedSelectStatement getInstrumentedSelectStatement(String originalSql) {
        if (this.config.isCacheInstrumentedSql()) {
            try {
                return this.instrumentSqlCache.get(originalSql);
            } catch (Throwable e) {
                // ignore it
            }
        }
        return null;
    }

    private InstrumentedSelectStatement getInstrumentedSelectStatementIfPresent(String originalSql) {
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

    public SQLInstrumentConfig getConfig() {
        return this.config;
    }



    @NonNull
    public void setConfig(final SQLInstrumentConfig config) {
        this.config = config;
    }

    public void setDialectRegistry(final DialectRegistry dialectRegistry) {
        this.dialectRegistry = dialectRegistry;
    }

    public DialectRegistry getDialectRegistry(){
        return this.dialectRegistry;
    }
}
