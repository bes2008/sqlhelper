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
import com.jn.langx.util.reflect.Reflects;
import com.jn.sqlhelper.common.batch.BatchResult;
import com.jn.sqlhelper.common.batch.BatchType;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;

import java.sql.SQLException;
import java.util.List;

public class JdbcBatchUpdater<E> extends MybatisBatchUpdater<E> {

    @Override
    public BatchResult<E> batchUpdate(MybatisBatchStatement statement, List<E> entities) throws SQLException {
        Preconditions.checkNotNull(statement);
        Preconditions.checkArgument(statement.getBatchType() == BatchType.SIMPLE);
        Preconditions.checkNotNull(sessionFactory);
        Preconditions.checkNotNull(statement.getMapperClass());

        SqlSession session = sessionFactory.openSession(ExecutorType.BATCH);
        BatchResult<E> result = new BatchResult<E>();
        result.setParameters(entities);
        result.setStatement(statement);

        String statementId = statement.getSql();
        String statementIdFQN = Reflects.getFQNClassName(statement.getMapperClass()) + "." + statementId;
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
                    result.addThrowable(ex);
                }
            }
            session.commit(true);
        } catch (Exception ex) {
            result.addThrowable(ex);
            updated = 0;
        } finally {
            session.close();
        }
        result.setRowsAffected(updated);
        return result;
    }
}
