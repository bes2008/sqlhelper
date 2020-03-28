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

import com.jn.langx.util.Throwables;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.struct.Entry;
import com.jn.langx.util.struct.Pair;
import com.jn.sqlhelper.dialect.pagination.PagedPreparedParameterSetter;
import com.jn.sqlhelper.dialect.pagination.QueryParameters;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.*;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PreparedStatementCreatorImpl implements PreparedStatementCreator, PreparedStatementSetter, SqlProvider, ParameterDisposer, PagedPreparedParameterSetter {

    private final String actualSql;

    private final List parameters;

    private List<SqlParameter> declaredParameters;


    private int resultSetType = ResultSet.TYPE_FORWARD_ONLY;

    private boolean updatableResults = false;

    private boolean returnGeneratedKeys = false;

    @Nullable
    private String[] generatedKeysColumnNames;

    public PreparedStatementCreatorImpl(String actualSql, List<SqlParameter> declaredParameters, List<?> parameters) {
        this.actualSql = actualSql;
        Assert.notNull(parameters, "Parameters List must not be null");
        this.parameters = parameters;
        if (this.parameters.size() != declaredParameters.size()) {
            // Account for named parameters being used multiple times
            Set<String> names = new HashSet<String>();
            for (int i = 0; i < parameters.size(); i++) {
                Object param = parameters.get(i);
                if (param instanceof SqlParameterValue) {
                    names.add(((SqlParameterValue) param).getName());
                } else {
                    names.add("Parameter #" + i);
                }
            }
            if (names.size() != declaredParameters.size()) {
                throw new InvalidDataAccessApiUsageException(
                        "SQL [" + actualSql + "]: given " + names.size() +
                                " parameters but expected " + declaredParameters.size());
            }
        }
    }

    @Override
    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        PreparedStatement ps;
        if (generatedKeysColumnNames != null || returnGeneratedKeys) {
            if (generatedKeysColumnNames != null) {
                ps = con.prepareStatement(this.actualSql, generatedKeysColumnNames);
            } else {
                ps = con.prepareStatement(this.actualSql, PreparedStatement.RETURN_GENERATED_KEYS);
            }
        } else if (resultSetType == ResultSet.TYPE_FORWARD_ONLY && !updatableResults) {
            ps = con.prepareStatement(this.actualSql);
        } else {
            ps = con.prepareStatement(this.actualSql, resultSetType,
                    updatableResults ? ResultSet.CONCUR_UPDATABLE : ResultSet.CONCUR_READ_ONLY);
        }
        setValues(ps);
        return ps;
    }

    @Override
    public void setValues(PreparedStatement ps) throws SQLException {
        // Set arguments: Does nothing if there are no parameters.
        int sqlColIndx = 1;
        for (int i = 0; i < this.parameters.size(); i++) {
            Object in = this.parameters.get(i);
            SqlParameter declaredParameter;
            // SqlParameterValue overrides declared parameter meta-data, in particular for
            // independence from the declared parameter position in case of named parameters.
            if (in instanceof SqlParameterValue) {
                SqlParameterValue paramValue = (SqlParameterValue) in;
                in = paramValue.getValue();
                declaredParameter = paramValue;
            } else {
                if (declaredParameters.size() <= i) {
                    throw new InvalidDataAccessApiUsageException(
                            "SQL [" + actualSql + "]: unable to access parameter number " + (i + 1) +
                                    " given only " + declaredParameters.size() + " parameters");

                }
                declaredParameter = declaredParameters.get(i);
            }
            if (in instanceof Iterable && declaredParameter.getSqlType() != Types.ARRAY) {
                Iterable<?> entries = (Iterable<?>) in;
                for (Object entry : entries) {
                    if (entry instanceof Object[]) {
                        Object[] valueArray = (Object[]) entry;
                        for (Object argValue : valueArray) {
                            StatementCreatorUtils.setParameterValue(ps, sqlColIndx++, declaredParameter, argValue);
                        }
                    } else {
                        StatementCreatorUtils.setParameterValue(ps, sqlColIndx++, declaredParameter, entry);
                    }
                }
            } else {
                StatementCreatorUtils.setParameterValue(ps, sqlColIndx++, declaredParameter, in);
            }
        }
    }

    @Override
    public String getSql() {
        return actualSql;
    }

    @Override
    public void cleanupParameters() {
        StatementCreatorUtils.cleanupParameters(this.parameters);
    }

    @Override
    public String toString() {
        return "PreparedStatementCreator: sql=[" + actualSql + "]; parameters=" + this.parameters;
    }

    /**
     * Set whether to use prepared statements that return a specific type of ResultSet.
     *
     * @param resultSetType the ResultSet type
     * @see java.sql.ResultSet#TYPE_FORWARD_ONLY
     * @see java.sql.ResultSet#TYPE_SCROLL_INSENSITIVE
     * @see java.sql.ResultSet#TYPE_SCROLL_SENSITIVE
     */
    public void setResultSetType(int resultSetType) {
        this.resultSetType = resultSetType;
    }

    /**
     * Set whether to use prepared statements capable of returning updatable ResultSets.
     */
    public void setUpdatableResults(boolean updatableResults) {
        this.updatableResults = updatableResults;
    }

    /**
     * Set whether prepared statements should be capable of returning auto-generated keys.
     */
    public void setReturnGeneratedKeys(boolean returnGeneratedKeys) {
        this.returnGeneratedKeys = returnGeneratedKeys;
    }

    /**
     * Set the column names of the auto-generated keys.
     */
    public void setGeneratedKeysColumnNames(String... names) {
        this.generatedKeysColumnNames = names;
    }

    @Override
    public int setBeforeSubqueryParameters(PreparedStatement statement, QueryParameters queryParameters, int startIndex) throws SQLException {
        List<Pair<SqlParameter, Object>> parameters = flatParameters();
        return setSqlParameters(statement, Collects.limit(parameters, queryParameters.getBeforeSubqueryParameterCount()), startIndex);
    }

    @Override
    public int setSubqueryParameters(PreparedStatement statement, QueryParameters queryParameters, int startIndex) throws SQLException {
        List<Pair<SqlParameter, Object>> parameters = flatParameters();
        Collection<Pair<SqlParameter, Object>> p = Pipeline.of(parameters)
                .limit(parameters.size() - queryParameters.getAfterSubqueryParameterCount())
                .skip(queryParameters.getBeforeSubqueryParameterCount()).asList();
        return setSqlParameters(statement, p, startIndex);
    }

    @Override
    public int setAfterSubqueryParameters(PreparedStatement statement, QueryParameters queryParameters, int startIndex) throws SQLException {
        List<Pair<SqlParameter, Object>> parameters = flatParameters();
        return setSqlParameters(statement, Collects.skip(parameters, parameters.size() - queryParameters.getAfterSubqueryParameterCount()), startIndex);
    }

    @Override
    public int setOriginalParameters(PreparedStatement statement, QueryParameters queryParameters, int startIndex) throws SQLException {
        List<Pair<SqlParameter, Object>> parameters = flatParameters();
        return setSqlParameters(statement, parameters, 1);
    }

    private int setSqlParameters(final PreparedStatement statement, Collection<Pair<SqlParameter, Object>> parameters, final int startIndex) {
        Collects.forEach(parameters, new Consumer2<Integer, Pair<SqlParameter, Object>>() {
            @Override
            public void accept(Integer index, Pair<SqlParameter, Object> pair) {
                try {
                    StatementCreatorUtils.setParameterValue(statement, index + startIndex, pair.getKey(), pair.getValue());
                } catch (SQLException ex) {
                    throw Throwables.wrapAsRuntimeException(ex);
                }
            }
        });
        return parameters.size();
    }

    private List<Pair<SqlParameter, Object>> flatParameters() {
        final List<Pair<SqlParameter, Object>> ret = Collects.emptyArrayList();
        Collects.forEach(this.parameters, new Consumer2<Integer, Object>() {
            @Override
            public void accept(Integer i, Object in) {
                SqlParameter declaredParameter;
                // SqlParameterValue overrides declared parameter meta-data, in particular for
                // independence from the declared parameter position in case of named parameters.
                if (in instanceof SqlParameterValue) {
                    SqlParameterValue paramValue = (SqlParameterValue) in;
                    in = paramValue.getValue();
                    declaredParameter = paramValue;
                } else {
                    if (declaredParameters.size() <= i) {
                        throw new InvalidDataAccessApiUsageException(
                                "SQL [" + getSql() + "]: unable to access parameter number " + (i + 1) +
                                        " given only " + declaredParameters.size() + " parameters");

                    }
                    declaredParameter = declaredParameters.get(i);
                }
                if (in instanceof Collection && declaredParameter.getSqlType() != Types.ARRAY) {
                    Collection<?> entries = (Collection<?>) in;
                    for (Object entry : entries) {
                        if (entry instanceof Object[]) {
                            Object[] valueArray = ((Object[]) entry);
                            for (Object argValue : valueArray) {
                                ret.add(new Entry<SqlParameter, Object>(declaredParameter, argValue));
                            }
                        } else {
                            ret.add(new Entry<SqlParameter, Object>(declaredParameter, entry));
                        }
                    }
                } else {
                    ret.add(new Entry<SqlParameter, Object>(declaredParameter, in));
                }
            }
        });
        return ret;
    }

    public static class Factory {

        private static Field actualSqlField; // String
        private static Field parametersField; //List<?>
        private static Field declaredParametersField; //List<SqlParameter>
        private static Field resultSetTypeField; // int, default:  ResultSet.TYPE_FORWARD_ONLY
        private static Field updatableResultsField; //boolean, default: false
        private static Field returnGeneratedKeysField; //boolean, default: false
        @Nullable
        private static Field generatedKeysColumnNamesField; // String[]

        public static PreparedStatementCreatorImpl creator(Object instance) {
            Class clazz = instance.getClass();
            if (actualSqlField == null) {
                actualSqlField = Reflects.getDeclaredField(clazz, "actualSql");
            }
            if (parametersField == null) {
                parametersField = Reflects.getDeclaredField(clazz, "parameters");
            }

            String actualSql = Reflects.getFieldValue(actualSqlField, instance, true, false);
            List<?> parameters = Reflects.getFieldValue(parametersField, instance, true, false);
            return new PreparedStatementCreatorImpl(actualSql, null, parameters);
        }

    }


}