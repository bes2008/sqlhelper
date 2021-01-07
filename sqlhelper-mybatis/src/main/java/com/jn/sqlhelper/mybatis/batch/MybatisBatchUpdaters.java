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

package com.jn.sqlhelper.mybatis.batch;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.*;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.concurrent.completion.CompletableFuture;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Supplier;
import com.jn.langx.util.function.Supplier0;
import com.jn.sqlhelper.common.batch.BatchMode;
import com.jn.sqlhelper.common.batch.BatchResult;
import com.jn.sqlhelper.dialect.Dialect;
import com.jn.sqlhelper.dialect.DialectRegistry;
import com.jn.sqlhelper.mybatis.MybatisUtils;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class MybatisBatchUpdaters {
    private static final Logger logger = LoggerFactory.getLogger(MybatisBatchUpdaters.class);

    public static <E> MybatisBatchUpdater<E> createSimpleBatchUpdater(@NonNull SqlSessionFactory sessionFactory) {
        return createBatchUpdater(sessionFactory, BatchMode.SIMPLE);
    }

    public static <E> MybatisBatchUpdater<E> createBatchSqlBatchUpdater(@NonNull SqlSessionFactory sessionFactory) {
        return createBatchUpdater(sessionFactory, BatchMode.BATCH_SQL);
    }

    public static <E> MybatisBatchUpdater<E> createJdbcBatchUpdater(@NonNull SqlSessionFactory sessionFactory) {
        return createBatchUpdater(sessionFactory, BatchMode.JDBC_BATCH);
    }

    public static <E> MybatisBatchUpdater<E> createBatchUpdater(@NonNull SqlSessionFactory sessionFactory, @Nullable BatchMode batchType) {
        MybatisBatchUpdater<E> updater = null;
        if (batchType != null) {
            switch (batchType) {
                case SIMPLE:
                    updater = new SimpleBatchUpdater<E>();
                    break;
                case BATCH_SQL:
                    updater = new BatchSqlBatchUpdater<E>();
                    break;
                case JDBC_BATCH:
                    updater = new JdbcBatchUpdater<E>();
                    break;
                default:
                    break;
            }
        }
        if (Objs.isNotNull(updater)) {
            updater.setSessionFactory(sessionFactory);
        }
        return updater;
    }

    public static <E> BatchResult<E> batchUpdate(@NonNull SqlSessionFactory sessionFactory,
                                                 @NonNull String statementIdFQN,
                                                 @Nullable BatchMode batchMode,
                                                 List<E> entities) throws SQLException {
        return batchUpdate(sessionFactory, statementIdFQN, batchMode, entities, 100, null);
    }


    public static <E> BatchResult<E> batchUpdate(@NonNull SqlSessionFactory sessionFactory,
                                                 @NonNull String statementIdFQN,
                                                 @Nullable BatchMode batchMode,
                                                 List<E> entities,
                                                 int batchSize,
                                                 ExecutorService executor) throws SQLException {
        return batchUpdate(sessionFactory, batchMode, new MybatisBatchStatement(batchMode, statementIdFQN), entities, batchSize, executor);
    }

    public static <E> BatchResult<E> batchUpdate(@NonNull SqlSessionFactory sessionFactory,
                                                 @NonNull Class mapperClass,
                                                 @NonNull String statementId,
                                                 @Nullable BatchMode batchMode,
                                                 List<E> entities) throws SQLException {
        return batchUpdate(sessionFactory, mapperClass, statementId, batchMode, entities, 100, null);
    }


    public static <E> BatchResult<E> batchUpdate(@NonNull SqlSessionFactory sessionFactory,
                                                 @NonNull Class mapperClass,
                                                 @NonNull String statementId,
                                                 @Nullable BatchMode batchMode,
                                                 List<E> entities,
                                                 int batchSize,
                                                 @Nullable ExecutorService executor) throws SQLException {
        return batchUpdate(sessionFactory, batchMode, new MybatisBatchStatement(batchMode, mapperClass, statementId), entities, batchSize, executor);
    }

    public static <E> BatchResult<E> batchUpdate(@NonNull final SqlSessionFactory sessionFactory,
                                                 @Nullable final BatchMode batchMode,
                                                 @NonNull final MybatisBatchStatement statement,
                                                 List<E> entities,
                                                 int batchSize,
                                                 @Nullable ExecutorService executor) throws SQLException {

        batchSize = batchSize < 1 ? 100 : batchSize;
        final BatchResult<E> result = new BatchResult<E>();
        result.setStatement(statement);
        result.setParameters(entities);
        result.setRowsAffected(0);
        if (Emptys.isAnyEmpty(entities, statement, sessionFactory)) {
            return result;
        }

        List<List<E>> entitiesList = Collects.partitionBySize(entities, batchSize);

        List<CompletableFuture<BatchResult<E>>> futures = Collects.emptyArrayList();

        for (List<E> es : entitiesList) {
            final List<E> segment = es;
            futures.add(
                    (executor == null ? CompletableFuture.supplyAsync(new Supplier0<BatchResult<E>>() {
                        @Override
                        public BatchResult<E> get() {
                            try {
                                return batch(sessionFactory, batchMode, statement, segment);
                            } catch (SQLException e) {
                                throw Throwables.wrapAsRuntimeException(e);
                            }
                        }
                    }) : CompletableFuture.supplyAsync(new Supplier0<BatchResult<E>>() {
                        @Override
                        public BatchResult<E> get() {
                            try {
                                return batch(sessionFactory, batchMode, statement, segment);
                            } catch (SQLException e) {
                                throw Throwables.wrapAsRuntimeException(e);
                            }
                        }
                    }, executor))
                            .exceptionally(new Function<Throwable, BatchResult<E>>() {
                                @Override
                                public BatchResult<E> apply(Throwable throwable) {
                                    BatchResult<E> result = new BatchResult<E>();
                                    result.setParameters(segment);
                                    result.setRowsAffected(0);
                                    result.setStatement(statement);
                                    result.setThrowables(Collects.newArrayList(throwable));
                                    return result;
                                }
                            })
                            .whenCompleteAsync(new Consumer2<BatchResult<E>, Throwable>() {
                                @Override
                                public void accept(BatchResult<E> batchResult0, Throwable throwable) {
                                    if (batchResult0 != null) {
                                        result.setRowsAffected(result.getRowsAffected() + batchResult0.getRowsAffected());
                                        result.getThrowables().addAll(batchResult0.getThrowables());
                                    }
                                    if (throwable != null) {
                                        result.getThrowables().add(throwable);
                                    }
                                }
                            }));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();
        return result;
    }


    /**
     * @param sessionFactory 这个sessionFactory 不能是 DynamicSqlSessionFactory, 如果是你拿到的是DynamicSqlSessionFactory，可以基于 SqlSessionFactoryProvider来进行帮忙获取真实的session factory
     * @param batchMode
     * @param statement
     * @param entities
     * @param <E>
     * @return
     * @throws SQLException
     */
    private static <E> BatchResult<E> batch(@NonNull SqlSessionFactory sessionFactory,
                                            @Nullable BatchMode batchMode,
                                            final MybatisBatchStatement statement,
                                            List<E> entities) throws SQLException {
        Preconditions.checkArgument(Emptys.isNotEmpty(entities));

        // build batch statement
        Preconditions.checkArgument(hasStatement(sessionFactory, statement), new Supplier<Object[], String>() {
            @Override
            public String get(Object[] objects) {
                return StringTemplates.formatWithPlaceholder("The statement {} is not exists", statement.getSql());
            }
        });

        if (batchMode != null) {
            return MybatisBatchUpdaters.<E>createBatchUpdater(sessionFactory, batchMode).batchUpdate(statement, entities);
        }

        // find dialect
        Configuration configuration = sessionFactory.getConfiguration();
        String databaseId = configuration.getDatabaseId();
        Dialect dialect = null;
        if (!Strings.isEmpty(databaseId)) {
            dialect = DialectRegistry.getInstance().getDialectByName(databaseId);
        }
        if (Objs.isNull(dialect)) {
            SqlSession session = sessionFactory.openSession();
            Connection connection = session.getConnection();
            dialect = DialectRegistry.getInstance().getDialectByDatabaseMetadata(connection.getMetaData());
            session.close();
        }


        boolean supportsBatchSqlMode = dialect != null && dialect.isSupportsBatchSql();
        MybatisBatchUpdater<E> updater = null;
        BatchResult<E> result = null;
        if (supportsBatchSqlMode) {
            statement.setBatchMode(BatchMode.BATCH_SQL);
            updater = createBatchSqlBatchUpdater(sessionFactory);
            result = updater.batchUpdate(statement, entities);
            if (!result.hasThrowable()) {
                return result;
            }
            logger.warn("Error when execute batch update based on database's batch sql, may be the statement {} not a batch sql, will use jdbc batch method execute it. error: {}", statement.getSql(), result.getThrowables().get(0));
        }
        boolean supportsJdbcBatch = dialect != null && dialect.isSupportsBatchUpdates();
        if (supportsJdbcBatch) {
            statement.setBatchMode(BatchMode.JDBC_BATCH);
            updater = createJdbcBatchUpdater(sessionFactory);
            result = updater.batchUpdate(statement, entities);
            if (result.hasThrowable()) {
                logger.warn("Error when execute batch update based jdbc batch mode, statement: {}, errors:", statement.getSql());
                Collects.forEach(result.getThrowables(), new Consumer2<Integer, Throwable>() {
                    @Override
                    public void accept(Integer index, Throwable throwable) {
                        logger.warn("errors[{}]", index, throwable);
                    }
                });
            } else {
                return result;
            }
        }
        statement.setBatchMode(BatchMode.SIMPLE);
        updater = createSimpleBatchUpdater(sessionFactory);
        result = updater.batchUpdate(statement, entities);
        if (result.hasThrowable()) {
            logger.warn("Error when execute batch update based simple batch mode, statement: {}, errors", statement.getSql());
            Collects.forEach(result.getThrowables(), new Consumer2<Integer, Throwable>() {
                @Override
                public void accept(Integer index, Throwable throwable) {
                    logger.warn("errors[{}]", index, throwable);
                }
            });
        }
        return result;
    }

    public static boolean hasStatement(@NonNull SqlSessionFactory sessionFactory, @NonNull MybatisBatchStatement statement) {
        return MybatisUtils.hasStatement(sessionFactory, statement.getSql());
    }
}
