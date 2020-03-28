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
import com.jn.langx.util.struct.Entry;
import com.jn.langx.util.struct.Pair;
import com.jn.sqlhelper.dialect.pagination.PagedPreparedParameterSetter;
import com.jn.sqlhelper.dialect.pagination.QueryParameters;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.*;
import org.springframework.util.Assert;

import java.sql.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NamedParameterPreparedStatementCreator implements PreparedStatementCreator, PreparedStatementSetter, PagedPreparedParameterSetter, SqlProvider, ParameterDisposer {
    private final String actualSql;

    private final List parameters;

    private NamedParameterPreparedStatementCreatorFactory factory;

    public NamedParameterPreparedStatementCreator(String actualSql, List<?> parameters, NamedParameterPreparedStatementCreatorFactory factory) {
        this.actualSql = actualSql;
        Assert.notNull(parameters, "Parameters List must not be null");
        this.parameters = parameters;
        this.factory = factory;
        // Account for named parameters being used multiple times

        if (this.parameters.size() != factory.getDeclaredParameters().size()) {
            Set<String> names = new HashSet<String>();
            for (int i = 0; i < parameters.size(); i++) {
                Object param = parameters.get(i);
                if (param instanceof SqlParameterValue) {
                    names.add(((SqlParameterValue) param).getName());
                } else {
                    names.add("Parameter #" + i);
                }
            }
            if (names.size() != factory.getDeclaredParameters().size()) {
                throw new InvalidDataAccessApiUsageException(
                        "SQL [" + getSql() + "]: given " + names.size() +
                                " parameters but expected " + factory.getDeclaredParameters().size());
            }
        }
    }

    @Override
    public void cleanupParameters() {
        StatementCreatorUtils.cleanupParameters(this.parameters);
    }

    @Override
    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        PreparedStatement ps;
        if (factory.getGeneratedKeysColumnNames() != null || factory.isReturnGeneratedKeys()) {
            if (factory.getGeneratedKeysColumnNames() != null) {
                ps = con.prepareStatement(this.actualSql, factory.getGeneratedKeysColumnNames());
            } else {
                ps = con.prepareStatement(this.actualSql, PreparedStatement.RETURN_GENERATED_KEYS);
            }
        } else if (factory.getResultSetType() == ResultSet.TYPE_FORWARD_ONLY && !factory.isUpdatableResults()) {
            ps = con.prepareStatement(this.actualSql);
        } else {
            ps = con.prepareStatement(this.actualSql, factory.getResultSetType(), factory.isUpdatableResults() ? ResultSet.CONCUR_UPDATABLE : ResultSet.CONCUR_READ_ONLY);
        }
        setValues(ps);
        return ps;
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
                    if (factory.getDeclaredParameters().size() <= i) {
                        throw new InvalidDataAccessApiUsageException(
                                "SQL [" + getSql() + "]: unable to access parameter number " + (i + 1) +
                                        " given only " + factory.getDeclaredParameters().size() + " parameters");

                    }
                    declaredParameter = factory.getDeclaredParameters().get(i);
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
                if (factory.getDeclaredParameters().size() <= i) {
                    throw new InvalidDataAccessApiUsageException(
                            "SQL [" + getSql() + "]: unable to access parameter number " + (i + 1) +
                                    " given only " + factory.getDeclaredParameters().size() + " parameters");

                }
                declaredParameter = factory.getDeclaredParameters().get(i);
            }
            if (in instanceof Collection && declaredParameter.getSqlType() != Types.ARRAY) {
                Collection<?> entries = (Collection<?>) in;
                for (Object entry : entries) {
                    if (entry instanceof Object[]) {
                        Object[] valueArray = ((Object[]) entry);
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

    public List getParameters() {
        return parameters;
    }

    public NamedParameterPreparedStatementCreatorFactory getFactory() {
        return factory;
    }

    public void setFactory(NamedParameterPreparedStatementCreatorFactory factory) {
        this.factory = factory;
    }


}
