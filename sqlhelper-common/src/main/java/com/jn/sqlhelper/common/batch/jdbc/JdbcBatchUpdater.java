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

package com.jn.sqlhelper.common.batch.jdbc;

import com.jn.langx.util.Preconditions;
import com.jn.sqlhelper.common.batch.BatchResult;
import com.jn.sqlhelper.common.batch.BatchStatement;
import com.jn.sqlhelper.common.batch.BatchType;
import com.jn.sqlhelper.common.batch.BatchUpdater;
import com.jn.sqlhelper.common.statement.PreparedStatementSetter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class JdbcBatchUpdater<E> implements BatchUpdater<E> {
    Connection connection;
    PreparedStatementSetter<E> setter;

    @Override
    public BatchResult<E> batchUpdate(BatchStatement statement, List<E> parametersList) throws SQLException {
        Preconditions.checkNotNull(statement);
        Preconditions.checkArgument(statement.getBatchType() == BatchType.JDBC_BATCH);
        PreparedStatement pstmt = connection.prepareStatement(statement.getSql());
        for (int i = 0; i < parametersList.size(); i++) {
            setter.setParameters(pstmt, 1, parametersList.get(i));
            pstmt.addBatch();
        }
        int[] updateds = pstmt.executeBatch();
        BatchResult<E> result = new BatchResult<E>();
        result.setParameters(parametersList);
        result.setStatement(statement);
        result.setRowsAffected(updateds[0]);
        return result;
    }
}
