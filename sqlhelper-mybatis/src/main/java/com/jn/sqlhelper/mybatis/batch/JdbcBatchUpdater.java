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

import com.jn.easyjson.core.JSONBuilderProvider;
import com.jn.langx.util.Preconditions;
import com.jn.sqlhelper.common.batch.BatchResult;
import com.jn.sqlhelper.common.batch.BatchMode;
import com.jn.sqlhelper.common.ddl.model.DatabaseDescription;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class JdbcBatchUpdater<E> extends MybatisBatchUpdater<E> {
    private static final Logger logger = LoggerFactory.getLogger(JdbcBatchUpdater.class);

    @Override
    public BatchResult<E> batchUpdate(MybatisBatchStatement statement, List<E> entities) throws SQLException {
        Preconditions.checkNotNull(statement);
        Preconditions.checkArgument(statement.getBatchType() == BatchMode.SIMPLE);
        Preconditions.checkNotNull(sessionFactory);

        SqlSession session = sessionFactory.openSession(ExecutorType.BATCH);
        Connection connection = session.getConnection();
        DatabaseDescription databaseDescription = new DatabaseDescription(connection.getMetaData());
        if (!databaseDescription.supportsBatchUpdates()) {
            throw new UnsupportedOperationException("batch update");
        }
        BatchResult<E> result = new BatchResult<E>();
        result.setParameters(entities);
        result.setStatement(statement);

        String statementId = statement.getStatementId();
        String statementIdFQN = statement.getSql();
        int updated = 0;
        try {
            for (E entity : entities) {
                try {
                    if (statementId.contains(INSERT)) {
                        updated = updated + session.insert(statementIdFQN, entity);
                    } else if (statementId.contains(UPDATE)) {
                        updated = updated + session.update(statementIdFQN, entity);
                    } else if (statementId.contains(DELETE)) {
                        updated = updated + session.delete(statementIdFQN, entity);
                    }
                    updated++;
                } catch (Exception ex) {
                    logger.error("Error occur when execute batch statement: {} with parameter: {}", statementIdFQN, JSONBuilderProvider.simplest().toJson(entity));
                    result.addThrowable(ex);
                }
            }
            session.commit(true);
        } catch (Exception ex) {
            logger.error("Error occur when execute batch statement: {}", statementIdFQN);
            result.addThrowable(ex);
            updated = 0;
        } finally {
            session.close();
        }
        result.setRowsAffected(updated);
        return result;
    }
}
