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
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.reflect.Reflects;
import com.jn.sqlhelper.dialect.pagination.PagedPreparedParameterSetter;
import com.jn.sqlhelper.dialect.pagination.QueryParameters;
import org.springframework.jdbc.core.*;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Simple adapter for {@link PreparedStatementSetter} that applies a given array of arguments.
 */
public class ArgumentPreparedStatementSetter implements PreparedStatementSetter, ParameterDisposer, PagedPreparedParameterSetter {

    @Nullable
    private final Object[] args;


    /**
     * Create a new ArgPreparedStatementSetter for the given arguments.
     *
     * @param args the arguments to set
     */
    public ArgumentPreparedStatementSetter(@Nullable Object[] args) {
        this.args = args;
    }

    @Override
    public int setBeforeSubqueryParameters(PreparedStatement statement, QueryParameters queryParameters, int startIndex) throws SQLException {
        if (this.args != null) {
            Object[] args = Pipeline.of(this.args).limit(queryParameters.getBeforeSubqueryParameterCount()).toArray();
            return internalSetValues(statement, args, startIndex);
        }
        return 0;
    }

    @Override
    public int setSubqueryParameters(PreparedStatement statement, QueryParameters queryParameters, int startIndex) throws SQLException {
        if (this.args != null) {
            Object[] args = Pipeline.of(this.args)
                    .limit(this.args.length - queryParameters.getAfterSubqueryParameterCount())
                    .skip(queryParameters.getBeforeSubqueryParameterCount())
                    .toArray();
            return internalSetValues(statement, args, startIndex);
        }
        return 0;
    }

    @Override
    public int setAfterSubqueryParameters(PreparedStatement statement, QueryParameters queryParameters, int startIndex) throws SQLException {
        if (this.args != null) {
            Object[] args = Pipeline.of(this.args).skip(this.args.length - queryParameters.getAfterSubqueryParameterCount()).toArray();
            return internalSetValues(statement, args, startIndex);
        }
        return 0;
    }

    @Override
    public int setOriginalParameters(PreparedStatement statement, QueryParameters queryParameters, int startIndex) throws SQLException {
        return internalSetValues(statement, this.args, 1);
    }

    private int internalSetValues(PreparedStatement ps, Object[] args, int startIndex) throws SQLException {
        int count = 0;
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                doSetValue(ps, i + startIndex, arg);
                count++;
            }
        }
        return count;
    }

    @Override
    public void setValues(PreparedStatement ps) throws SQLException {
        internalSetValues(ps, this.args, 1);
    }

    /**
     * Set the value for prepared statements specified parameter index using the passed in value.
     * This method can be overridden by sub-classes if needed.
     *
     * @param ps                the PreparedStatement
     * @param parameterPosition index of the parameter position
     * @param argValue          the value to set
     * @throws SQLException if thrown by PreparedStatement methods
     */
    protected void doSetValue(PreparedStatement ps, int parameterPosition, Object argValue) throws SQLException {
        if (argValue instanceof SqlParameterValue) {
            SqlParameterValue paramValue = (SqlParameterValue) argValue;
            StatementCreatorUtils.setParameterValue(ps, parameterPosition, paramValue, paramValue.getValue());
        } else {
            StatementCreatorUtils.setParameterValue(ps, parameterPosition, SqlTypeValue.TYPE_UNKNOWN, argValue);
        }
    }

    @Override
    public void cleanupParameters() {
        StatementCreatorUtils.cleanupParameters(this.args);
    }

    public static class Factory {
        static Field argsField = null;

        public static ArgumentPreparedStatementSetter create(org.springframework.jdbc.core.ArgumentPreparedStatementSetter setter) {
            if (argsField == null) {
                argsField = Reflects.getDeclaredField(org.springframework.jdbc.core.ArgumentPreparedStatementSetter.class, "args");
                argsField.setAccessible(true);
            }
            Object[] args = Reflects.getFieldValue(argsField, setter, true, false);
            return new ArgumentPreparedStatementSetter(args);
        }
    }


}
