
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

package com.jn.sqlhelper.dialect;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * the prepared statement setter, just for the original parameters
 *
 * all parameters will slice 3 segments:
 *  |-----------------original parameters-----------------|
 *  |------before-----|-----subquery-----|------after-----|
 * @param <P> original parameters
 * @see SQLStatementInstrumentor#bindParameters(Dialect, PreparedStatement, PagedPreparedParameterSetter, QueryParameters, boolean)
 */
public interface PagedPreparedParameterSetter<P extends QueryParameters> {
    /**
     * set original parameters, what is before subquery partition
     *
     * @param statement       the sql statement
     * @param queryParameters all the original parameters
     * @param startIndex      the start index
     * @return the count of set in the invocation
     * @throws SQLException throw it if error
     */
    int setBeforeSubqueryParameters(final PreparedStatement statement, final P queryParameters, final int startIndex) throws SQLException;


    /**
     * set subquery parameters, it is not contains limit, offset parameters
     *
     * @param statement       the sql statement
     * @param queryParameters all the original parameters
     * @param startIndex      the start index
     * @return the count of set in the invocation
     * @throws SQLException throw it if error
     */
    int setSubqueryParameters(final PreparedStatement statement, final P queryParameters, final int startIndex) throws SQLException;

    /**
     * set original parameters, what is after subquery partition
     *
     * @param statement       the sql statement
     * @param queryParameters all the original parameters
     * @param startIndex      the start index
     * @return the count of set in the invocation
     * @throws SQLException throw it if error+
     */
    int setAfterSubqueryParameters(final PreparedStatement statement, final P queryParameters, final int startIndex) throws SQLException;

    /**
     * set original parameters, it is not contains limit, offset parameters
     *
     * @param statement       the sql statement
     * @param queryParameters all the original parameters
     * @param startIndex      the start index
     * @return the count of set in the invocation
     * @throws SQLException throw it if error
     */
    int setOriginalParameters(final PreparedStatement statement, final P queryParameters, final int startIndex) throws SQLException;
}
