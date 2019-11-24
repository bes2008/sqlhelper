package com.jn.sqlhelper.springjdbc.statement;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.collection.PrimitiveArrays;
import com.jn.sqlhelper.dialect.PagedPreparedParameterSetter;
import com.jn.sqlhelper.dialect.QueryParameters;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.ParameterDisposer;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.lang.Nullable;

/**
 * Simple adapter for {@link PreparedStatementSetter} that applies
 * given arrays of arguments and JDBC argument types.
 */
public class ArgumentTypePreparedStatementSetter implements PreparedStatementSetter, ParameterDisposer, PagedPreparedParameterSetter {

    @Nullable
    private final Object[] args;

    @Nullable
    private final int[] argTypes;


    /**
     * Create a new ArgTypePreparedStatementSetter for the given arguments.
     * @param args the arguments to set
     * @param argTypes the corresponding SQL types of the arguments
     */
    public ArgumentTypePreparedStatementSetter(@Nullable Object[] args, @Nullable int[] argTypes) {
        if ((args != null && argTypes == null) || (args == null && argTypes != null) ||
                (args != null && args.length != argTypes.length)) {
            throw new InvalidDataAccessApiUsageException("args and argTypes parameters must match");
        }
        this.args = args;
        this.argTypes = argTypes;
    }

    @Override
    public int setBeforeSubqueryParameters(PreparedStatement ps, QueryParameters queryParameters, int startIndex) throws SQLException {
        Object[] args = Pipeline.of(this.args).limit(queryParameters.getBeforeSubqueryParameterCount()).toArray();
        int[] argTypes = PrimitiveArrays.unwrap(Pipeline.<Integer>of(this.argTypes).limit(queryParameters.getBeforeSubqueryParameterCount()).toArray(Integer[].class), true);
        setPagedSubqueryValues(ps, args, argTypes, startIndex);
        return queryParameters.getBeforeSubqueryParameterCount();
    }

    @Override
    public int setSubqueryParameters(PreparedStatement ps, QueryParameters queryParameters, int startIndex) throws SQLException {
        if(this.args!=null) {
            Object[] args = Pipeline.of(this.args)
                    .limit(this.args.length - queryParameters.getAfterSubqueryParameterCount())
                    .skip(queryParameters.getBeforeSubqueryParameterCount())
                    .toArray();
            int[] argTypes = PrimitiveArrays.unwrap(
                    Pipeline.<Integer>of(this.argTypes)
                            .limit(this.args.length - queryParameters.getAfterSubqueryParameterCount())
                            .skip(queryParameters.getBeforeSubqueryParameterCount())
                            .toArray(Integer[].class), true);
            setPagedSubqueryValues(ps, args, argTypes, startIndex);
            return args.length;
        }
        return 0;
    }

    @Override
    public int setAfterSubqueryParameters(PreparedStatement ps, QueryParameters queryParameters, int startIndex) throws SQLException {
        Object[] args = Pipeline.of(this.args).skip(queryParameters.getAfterSubqueryParameterCount()).toArray();
        int[] argTypes = PrimitiveArrays.unwrap(Pipeline.<Integer>of(this.argTypes).skip(queryParameters.getAfterSubqueryParameterCount()).toArray(Integer[].class), true);
        setPagedSubqueryValues(ps, args, argTypes, startIndex);
        return queryParameters.getAfterSubqueryParameterCount();
    }

    private void setPagedSubqueryValues(PreparedStatement ps, Object[] args, int[] argTypes, int startIndex) throws SQLException {
        int parameterPosition = startIndex;
        if (this.args != null && argTypes != null) {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg instanceof Collection && argTypes[i] != Types.ARRAY) {
                    Collection<?> entries = (Collection<?>) arg;
                    for (Object entry : entries) {
                        if (entry instanceof Object[]) {
                            Object[] valueArray = ((Object[]) entry);
                            for (Object argValue : valueArray) {
                                doSetValue(ps, parameterPosition, argTypes[i], argValue);
                                parameterPosition++;
                            }
                        }
                        else {
                            doSetValue(ps, parameterPosition, argTypes[i], entry);
                            parameterPosition++;
                        }
                    }
                }
                else {
                    doSetValue(ps, parameterPosition, argTypes[i], arg);
                    parameterPosition++;
                }
            }
        }
    }


    @Override
    public int setOriginalParameters(PreparedStatement ps, QueryParameters queryParameters, int startIndex) throws SQLException {
        return _setValues(ps, 1);
    }


    @Override
    public void setValues(PreparedStatement ps) throws SQLException {
        _setValues(ps, 1);
    }

    private int _setValues(PreparedStatement ps, int startIndex) throws SQLException {
        int count = 0;
        int parameterPosition = startIndex;
        if (this.args != null && this.argTypes != null) {
            for (int i = 0; i < this.args.length; i++) {
                Object arg = this.args[i];
                if (arg instanceof Collection && this.argTypes[i] != Types.ARRAY) {
                    Collection<?> entries = (Collection<?>) arg;
                    for (Object entry : entries) {
                        if (entry instanceof Object[]) {
                            Object[] valueArray = ((Object[]) entry);
                            for (Object argValue : valueArray) {
                                doSetValue(ps, parameterPosition, this.argTypes[i], argValue);
                                parameterPosition++;
                                count++;
                            }
                        }
                        else {
                            doSetValue(ps, parameterPosition, this.argTypes[i], entry);
                            parameterPosition++;
                            count++;
                        }
                    }
                }
                else {
                    doSetValue(ps, parameterPosition, this.argTypes[i], arg);
                    parameterPosition++;
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Set the value for the prepared statement's specified parameter position using the passed in
     * value and type. This method can be overridden by sub-classes if needed.
     * @param ps the PreparedStatement
     * @param parameterPosition index of the parameter position
     * @param argType the argument type
     * @param argValue the argument value
     * @throws SQLException if thrown by PreparedStatement methods
     */
    protected void doSetValue(PreparedStatement ps, int parameterPosition, int argType, Object argValue)
            throws SQLException {
        StatementCreatorUtils.setParameterValue(ps, parameterPosition, argType, argValue);
    }

    @Override
    public void cleanupParameters() {
        StatementCreatorUtils.cleanupParameters(this.args);
    }

}
