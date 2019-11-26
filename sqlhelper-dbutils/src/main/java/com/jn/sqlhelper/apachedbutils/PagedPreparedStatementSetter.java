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

import com.jn.langx.util.Throwables;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.sqlhelper.dialect.PagedPreparedParameterSetter;
import com.jn.sqlhelper.dialect.pagination.PagedPreparedStatement;
import com.jn.sqlhelper.dialect.parameter.ArrayBasedQueryParameters;

import java.sql.PreparedStatement;
import java.sql.SQLException;

class PagedPreparedStatementSetter implements PagedPreparedParameterSetter<ArrayBasedQueryParameters> {
    private PreparedStatementSetter delegate;

    public PagedPreparedStatementSetter(PreparedStatementSetter setter) {
        delegate = setter;
    }

    @Override
    public int setOriginalParameters(PreparedStatement statement, ArrayBasedQueryParameters queryParameters, int startIndex) throws SQLException {
        if (delegate != null) {
            if ((statement instanceof PagedPreparedStatement)) {
                PagedPreparedStatement pps = (PagedPreparedStatement) statement;
                pps.setIndexOffset(startIndex >= 1 ? (startIndex - 1) : -1);
                delegate.setValues(statement);
                pps.setIndexOffset(-1);
                return pps.getSetParameterIndexes().size();
            }
            delegate.setValues(statement);
        }
        return 0;
    }

    @Override
    public int setBeforeSubqueryParameters(PreparedStatement statement, ArrayBasedQueryParameters queryParameters, int startIndex) throws SQLException {
        return setParams(statement, queryParameters.getBeforeSubqueryParameterValues(), startIndex);
    }

    @Override
    public int setSubqueryParameters(PreparedStatement statement, ArrayBasedQueryParameters queryParameters, int startIndex) throws SQLException {
        return setParams(statement, queryParameters.getSubqueryParameterValues(), startIndex);
    }

    @Override
    public int setAfterSubqueryParameters(PreparedStatement statement, ArrayBasedQueryParameters queryParameters, int startIndex) throws SQLException {
        return setParams(statement, queryParameters.getAfterSubqueryParameterValues(), startIndex);
    }

    private int setParams(final PreparedStatement statement, Object[] params, final int startIndex) {
        Collects.forEach(params, new Consumer2<Integer, Object>() {
            @Override
            public void accept(Integer index, Object value) {
                try {
                    statement.setObject(startIndex + index, value);
                } catch (SQLException ex) {
                    throw Throwables.wrapAsRuntimeException(ex);
                }
            }
        });
        return params.length;
    }
}
