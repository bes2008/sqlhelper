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

import com.jn.langx.util.Preconditions;
import com.jn.sqlhelper.common.batch.BatchMode;
import com.jn.sqlhelper.common.batch.BatchResult;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class SimpleBatchUpdater<E> extends MybatisBatchUpdater<E> {
    private static final Logger logger = LoggerFactory.getLogger(SimpleBatchUpdater.class);

    @Override
    public BatchResult<E> batchUpdate(MybatisBatchStatement statement, List<E> entities) throws SQLException {
        Preconditions.checkNotNull(sessionFactory);
        Preconditions.checkNotNull(statement);
        Preconditions.checkArgument(statement.getBatchMode() == BatchMode.SIMPLE);
        SqlSession session = sessionFactory.openSession(true);

        BatchResult<E> result = new BatchResult<E>();
        result.setParameters(entities);
        result.setStatement(statement);
        String statementId = statement.getStatementId();
        String statementIdFQN = statement.getSql();
        int affectedRows = 0;
        try {
            for (E entity : entities) {
                int updated = 0;
                try {
                    if (statementId.contains(INSERT)) {
                        updated = session.insert(statementIdFQN, entity);
                    } else if (statementId.contains(UPDATE)) {
                        updated = session.update(statementIdFQN, entity);
                    } else if (statementId.contains(DELETE)) {
                        updated = session.delete(statementIdFQN, entity);
                    } else {
                        updated = session.update(statementIdFQN, entity);
                    }
                } catch (Exception ex) {
                    logger.error("Error occur when execute batch statement: {} with parameter: {}", statementIdFQN, entity.toString());
                    result.addThrowable(ex);
                }
                if (updated < 0) {
                    logger.warn("the affectedRows < 0 , maybe your default executor type is not simple");
                    updated = 1;
                }
                affectedRows += updated;
            }
            session.commit(true);
        } catch (Exception ex) {
            logger.error("Error occur when execute batch statement: {}", statementIdFQN);
            result.addThrowable(ex);
        } finally {
            session.close();
        }

        result.setRowsAffected(affectedRows);
        return result;
    }
}
