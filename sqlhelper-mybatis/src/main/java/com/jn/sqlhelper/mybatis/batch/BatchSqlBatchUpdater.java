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

import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.reflect.Reflects;
import com.jn.sqlhelper.common.batch.BatchResult;
import com.jn.sqlhelper.common.batch.BatchStatement;
import com.jn.sqlhelper.common.batch.BatchType;
import org.apache.ibatis.session.SqlSession;

import java.sql.SQLException;
import java.util.List;

public class BatchSqlBatchUpdater<E> extends MybatisBatchUpdater<E> {

    @Override
    public BatchResult batchUpdate(BatchStatement statement, List<E> beans) throws SQLException {
        Preconditions.checkNotNull(statement);
        Preconditions.checkArgument(statement.getBatchType() == BatchType.JDBC_BATCH);
        Preconditions.checkNotNull(statement.getSql(), "Sql statement id is null");
        Preconditions.checkArgument(Emptys.isNotEmpty(statement.getSql()), "the sql id is null");

        Preconditions.checkNotNull(sessionFactory);
        Preconditions.checkNotNull(mapperClass);

        SqlSession session = sessionFactory.openSession(true);
        String statementId = statement.getSql();
        statementId = Reflects.getFQNClassName(mapperClass) + "." + statementId;
        session.update(statementId, beans);
        BatchResult<E> result = new BatchResult<E>();
        result.setParameters(beans);
        result.setStatement(statement);
        return result;
    }
}
