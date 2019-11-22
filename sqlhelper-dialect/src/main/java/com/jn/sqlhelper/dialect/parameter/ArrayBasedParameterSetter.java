
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

package com.jn.sqlhelper.dialect.parameter;

import com.jn.sqlhelper.dialect.PagedPreparedParameterSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;


public class ArrayBasedParameterSetter implements PagedPreparedParameterSetter<ArrayBasedQueryParameters> {
    @Override
    public int setOriginalParameters(PreparedStatement statement, ArrayBasedQueryParameters queryParameters, int startIndex) throws SQLException {
        if (queryParameters.getParameterValuesSize() > 0) {
            for (Object value : queryParameters.getParameterValues()) {
                statement.setObject(startIndex, value);
                startIndex++;
            }
        }
        return queryParameters.getParameterValuesSize();
    }


    @Override
    public int setBeforeSubqueryParameters(PreparedStatement statement, ArrayBasedQueryParameters queryParameters, int startIndex) throws SQLException {
        if (queryParameters.getBeforeSubqueryParameterCount() > 0) {
            for (Object value : queryParameters.getBeforeSubqueryParameterValues()) {
                statement.setObject(startIndex, value);
                startIndex++;
            }
        }
        return queryParameters.getBeforeSubqueryParameterCount();
    }

    @Override
    public int setSubqueryParameters(PreparedStatement statement, ArrayBasedQueryParameters parameters, int startIndex)
            throws SQLException {
        if (parameters.getSubqueryParameterValues().length > 0) {
            for (Object value : parameters.getSubqueryParameterValues()) {
                statement.setObject(startIndex, value);
                startIndex++;
            }
        }
        return parameters.getSubqueryParameterValues().length;
    }

    @Override
    public int setAfterSubqueryParameters(PreparedStatement statement, ArrayBasedQueryParameters queryParameters, int startIndex) throws SQLException {
        if (queryParameters.getAfterSubqueryParameterCount() > 0) {
            for (Object value : queryParameters.getAfterSubqueryParameterValues()) {
                statement.setObject(startIndex, value);
                startIndex++;
            }
        }
        return queryParameters.getAfterSubqueryParameterCount();
    }
}