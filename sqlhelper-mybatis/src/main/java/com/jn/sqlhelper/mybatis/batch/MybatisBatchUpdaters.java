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
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Objects;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.function.Supplier;
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
        if (Objects.isNotNull(updater)) {
            updater.setSessionFactory(sessionFactory);
        }
        return updater;
    }

    public static <E> BatchResult<E> batchUpdate(@NonNull SqlSessionFactory sessionFactory,
                                                 @NonNull String statementIdFQN,
                                                 @Nullable BatchMode batchMode,
                                                 List<E> entities) throws SQLException {
        final MybatisBatchStatement statement = new MybatisBatchStatement(batchMode, statementIdFQN);
        return batch(sessionFactory, batchMode, statement, entities);
    }


    public static <E> BatchResult<E> batchUpdate(@NonNull SqlSessionFactory sessionFactory,
                                                 @NonNull Class mapperClass,
                                                 @NonNull String statementId,
                                                 @Nullable BatchMode batchMode,
                                                 List<E> entities) throws SQLException {
        final MybatisBatchStatement statement = new MybatisBatchStatement(batchMode, mapperClass, statementId);
        return batch(sessionFactory, batchMode, statement, entities);
    }

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
        if (Objects.isNull(dialect)) {
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
