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

package com.jn.sqlhelper.common.statement;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ListPreparedStatementSetter implements PreparedStatementSetter<List> {
    @Override
    public void setParameters(final PreparedStatement statement, final int startIndex, List parameters) throws SQLException {
        for (int i = 0; i < parameters.size(); i++) {
            statement.setObject(startIndex + i, parameters.get(i));
        }
    }
}
