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
import com.jn.langx.util.Objects;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.function.Supplier;
import com.jn.sqlhelper.common.batch.BatchMode;
import com.jn.sqlhelper.common.batch.BatchResult;
import com.jn.sqlhelper.mybatis.MybatisUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class MyBatisBatchUpdaters {
    private static final Logger logger = LoggerFactory.getLogger(MyBatisBatchUpdaters.class);

    public static <E> MybatisBatchUpdater<E> simpleBatchUpdater(@NonNull SqlSessionFactory sessionFactory) {
        return batchUpdater(sessionFactory, BatchMode.SIMPLE);
    }

    public static <E> MybatisBatchUpdater<E> batchSqlBatchUpdater(@NonNull SqlSessionFactory sessionFactory) {
        return batchUpdater(sessionFactory, BatchMode.BATCH_SQL);
    }

    public static <E> MybatisBatchUpdater<E> jdbcBatchUpdater(@NonNull SqlSessionFactory sessionFactory) {
        return batchUpdater(sessionFactory, BatchMode.JDBC_BATCH);
    }

    public static <E> MybatisBatchUpdater<E> batchUpdater(@NonNull SqlSessionFactory sessionFactory, @Nullable BatchMode batchType) {
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

    public static <E> BatchResult<E> batch(@NonNull SqlSessionFactory sessionFactory,
                                           @NonNull Class mapperClass,
                                           @NonNull String statementId,
                                           @Nullable BatchMode batchMode,
                                           List<E> entities) throws SQLException {
        final MybatisBatchStatement statement = new MybatisBatchStatement(batchMode, mapperClass, statementId);
        Preconditions.checkArgument(hasStatement(sessionFactory, statement), new Supplier<Object[], String>() {
            @Override
            public String get(Object[] objects) {
                return StringTemplates.formatWithPlaceholder("The statement {} is not exists", statement.getSql());
            }
        });

        MybatisBatchUpdater<E> updater = batchUpdater(sessionFactory, batchMode);
        if (batchMode != null && updater != null) {
            return updater.batchUpdate(statement, entities);
        }
        BatchResult<E> result = null;
        updater = batchSqlBatchUpdater(sessionFactory);
        result = updater.batchUpdate(statement, entities);
        if (!result.hasThrowable()) {
            return result;
        }
        result = null;
        logger.warn("Error when execute batch update based on database's batch sql, may be the statement {} not a batch sql, will use jdbc batch method execute it. error: {}", statement.getSql(), result.getThrowables().get(0));
        updater = jdbcBatchUpdater(sessionFactory);
        try {
            result = updater.batchUpdate(statement, entities);
        } catch (UnsupportedOperationException e2) {
            logger.warn("the database is not supports jdbc update, will use the simple batch mode");
            updater = simpleBatchUpdater(sessionFactory);
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
        if (result != null && result.hasThrowable()) {
            logger.warn("Error when execute batch update based jdbc batch mode, statement: {}, errors:", statement.getSql());
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
