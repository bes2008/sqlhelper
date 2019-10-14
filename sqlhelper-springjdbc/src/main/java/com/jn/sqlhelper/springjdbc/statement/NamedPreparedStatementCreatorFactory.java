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

package com.jn.sqlhelper.springjdbc.statement;

import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.collection.Collects;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.SqlParameter;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NamedPreparedStatementCreatorFactory extends PreparedStatementCreatorFactory {
    /**
     * The SQL, which won't change when the parameters change.
     */
    private String sql;

    /**
     * List of SqlParameter objects (may not be {@code null}).
     */
    private List<SqlParameter> declaredParameters;

    private int resultSetType = ResultSet.TYPE_FORWARD_ONLY;

    private boolean updatableResults = false;

    private boolean returnGeneratedKeys = false;

    @Nullable
    private String[] generatedKeysColumnNames;


    /**
     * Create a new factory. Will need to add parameters via the
     * {@link #addParameter} method or have no parameters.
     *
     * @param sql the SQL statement to execute
     */
    public NamedPreparedStatementCreatorFactory(String sql) {
        this(sql, Collects.<SqlParameter>emptyLinkedList());
    }

    /**
     * Create a new factory with the given SQL and JDBC types.
     *
     * @param sql   the SQL statement to execute
     * @param types int array of JDBC types
     */
    public NamedPreparedStatementCreatorFactory(String sql, int... types) {
        this(sql, SqlParameter.sqlTypesToAnonymousParameterList(types));
    }

    /**
     * Create a new factory with the given SQL and parameters.
     *
     * @param sql                the SQL statement to execute
     * @param declaredParameters list of {@link SqlParameter} objects
     */
    public NamedPreparedStatementCreatorFactory(String sql, List<SqlParameter> declaredParameters) {
        super(sql, (List<SqlParameter>) null);
        this.sql = sql;
        this.declaredParameters = declaredParameters;
    }

    /**
     * Return a new PreparedStatementSetter for the given parameters.
     *
     * @param params list of parameters (may be {@code null})
     */
    @Override
    public PreparedStatementSetter newPreparedStatementSetter(List<?> params) {
        return new NamedPreparedStatementCreator(sql, params != null ? params : Collections.emptyList(), this);
    }

    /**
     * Return a new PreparedStatementSetter for the given parameters.
     *
     * @param params the parameter array (may be {@code null})
     */
    @Override
    public PreparedStatementSetter newPreparedStatementSetter(Object[] params) {
        return new NamedPreparedStatementCreator(sql, params != null ? Arrays.asList(params) : Collections.emptyList(), this);
    }

    /**
     * Return a new PreparedStatementCreator for the given parameters.
     *
     * @param params list of parameters (may be {@code null})
     */
    @Override
    public PreparedStatementCreator newPreparedStatementCreator(List<?> params) {
        return new NamedPreparedStatementCreator(sql, params != null ? params : Collections.emptyList(), this);
    }

    /**
     * Return a new PreparedStatementCreator for the given parameters.
     *
     * @param params the parameter array (may be {@code null})
     */
    @Override
    public PreparedStatementCreator newPreparedStatementCreator(Object[] params) {
        return new NamedPreparedStatementCreator(sql, params != null ? Arrays.asList(params) : Collections.emptyList(), this);
    }

    /**
     * Return a new PreparedStatementCreator for the given parameters.
     *
     * @param sqlToUse the actual SQL statement to use (if different from
     *                 the factory's, for example because of named parameter expanding)
     * @param params   the parameter array (may be {@code null})
     */
    @Override
    public PreparedStatementCreator newPreparedStatementCreator(String sqlToUse, Object[] params) {
        return new NamedPreparedStatementCreator(
                sqlToUse, params != null ? Arrays.asList(params) : Collections.emptyList(), this);
    }


    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<SqlParameter> getDeclaredParameters() {
        return declaredParameters;
    }

    public void setDeclaredParameters(List<SqlParameter> declaredParameters) {
        this.declaredParameters = declaredParameters;
    }

    public int getResultSetType() {
        return resultSetType;
    }

    @Override
    public void setResultSetType(int resultSetType) {
        this.resultSetType = resultSetType;
    }

    public boolean isUpdatableResults() {
        return updatableResults;
    }

    @Override
    public void setUpdatableResults(boolean updatableResults) {
        this.updatableResults = updatableResults;
    }

    public boolean isReturnGeneratedKeys() {
        return returnGeneratedKeys;
    }

    @Override
    public void setReturnGeneratedKeys(boolean returnGeneratedKeys) {
        this.returnGeneratedKeys = returnGeneratedKeys;
    }

    public String[] getGeneratedKeysColumnNames() {
        return generatedKeysColumnNames;
    }

    @Override
    public void setGeneratedKeysColumnNames(String[] generatedKeysColumnNames) {
        this.generatedKeysColumnNames = generatedKeysColumnNames;
    }
}
