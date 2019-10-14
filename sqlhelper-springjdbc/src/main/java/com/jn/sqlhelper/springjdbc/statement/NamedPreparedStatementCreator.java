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

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.*;
import org.springframework.util.Assert;

import java.sql.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NamedPreparedStatementCreator implements PreparedStatementCreator, PreparedStatementSetter, SqlProvider, ParameterDisposer {
    private final String actualSql;

    private final List parameters;

    private NamedPreparedStatementCreatorFactory factory;

    public NamedPreparedStatementCreator(String actualSql, List<?> parameters, NamedPreparedStatementCreatorFactory factory) {
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
            ps = con.prepareStatement(this.actualSql, factory.getResultSetType(),
                    factory.isUpdatableResults() ? ResultSet.CONCUR_UPDATABLE : ResultSet.CONCUR_READ_ONLY);
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

    public NamedPreparedStatementCreatorFactory getFactory() {
        return factory;
    }

    public void setFactory(NamedPreparedStatementCreatorFactory factory) {
        this.factory = factory;
    }
}
