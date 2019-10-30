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

package com.jn.sqlhelper.apachedbutils;

import com.jn.sqlhelper.dialect.PrepareParameterSetter;
import com.jn.sqlhelper.dialect.QueryParameters;
import com.jn.sqlhelper.dialect.pagination.PaginationPreparedStatement;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PaginationPreparedStatementSetter implements PrepareParameterSetter {
    private PreparedStatementSetter delegate;

    public PaginationPreparedStatementSetter(PreparedStatementSetter setter) {
        delegate = setter;
    }

    @Override
    public int setParameters(PreparedStatement statement, QueryParameters queryParameters, int startIndex) throws SQLException {
        if (delegate != null) {
            if ((statement instanceof PaginationPreparedStatement)) {
                PaginationPreparedStatement pps = (PaginationPreparedStatement) statement;
                pps.setIndexOffset(startIndex >= 1 ? (startIndex - 1) : -1);
                delegate.setValues(statement);
                pps.setIndexOffset(-1);
                return pps.getSetParameterIndexes().size();
            }
            delegate.setValues(statement);
        }
        return 0;
    }
}
