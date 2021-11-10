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

package com.jn.sqlhelper.common.batch.xjdbc;

import com.jn.langx.util.Preconditions;
import com.jn.sqlhelper.common.batch.BatchResult;
import com.jn.sqlhelper.common.batch.BatchStatement;
import com.jn.sqlhelper.common.batch.BatchMode;
import com.jn.sqlhelper.common.batch.BatchUpdater;
import com.jn.sqlhelper.common.statement.PreparedStatementSetter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class BatchSqlBatchUpdater<E, STATEMENT extends BatchStatement> implements BatchUpdater<E, STATEMENT> {
    Connection connection;
    PreparedStatementSetter setter;

    @Override
    public BatchResult batchUpdate(STATEMENT statement, List<E> parameters) throws SQLException {
        Preconditions.checkNotNull(statement);
        Preconditions.checkArgument(statement.getBatchMode() == BatchMode.BATCH_SQL);
        PreparedStatement pstmt = connection.prepareStatement(statement.getSql());
        setter.setParameters(pstmt, 1, parameters);
        int updatedRows = pstmt.executeUpdate();
        BatchResult result = new BatchResult();
        result.setRowsAffected(updatedRows);
        result.setStatement(statement);
        result.setParameters(parameters);
        return result;
    }
}
