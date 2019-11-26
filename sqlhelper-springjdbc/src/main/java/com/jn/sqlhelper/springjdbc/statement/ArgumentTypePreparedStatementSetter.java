package com.jn.sqlhelper.springjdbc.statement;


import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.collection.PrimitiveArrays;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.struct.Entry;
import com.jn.langx.util.struct.Pair;
import com.jn.sqlhelper.dialect.PagedPreparedParameterSetter;
import com.jn.sqlhelper.dialect.QueryParameters;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.ParameterDisposer;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.lang.Nullable;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.List;

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
     *
     * @param args     the arguments to set
     * @param argTypes the corresponding SQL types of the arguments
     */
    public ArgumentTypePreparedStatementSetter(@Nullable Object[] args, @Nullable int[] argTypes) {
        boolean argsIsNull = args == null;
        boolean argTypesIsNull = argTypes == null;
        if ((argsIsNull != argTypesIsNull) || (!argsIsNull && args.length != argTypes.length)) {
            throw new InvalidDataAccessApiUsageException("args and argTypes parameters must match");
        }

        this.args = args;
        this.argTypes = argTypes;
    }

    @Override
    public int setBeforeSubqueryParameters(PreparedStatement ps, QueryParameters queryParameters, int startIndex) throws SQLException {
        Pair<Object[], Integer[]> parametersPair = flatParameters();
        Object[] args = Pipeline.of(parametersPair.getKey()).limit(queryParameters.getBeforeSubqueryParameterCount()).toArray();
        int[] argTypes = PrimitiveArrays.unwrap(Pipeline.<Integer>of(parametersPair.getValue()).limit(queryParameters.getBeforeSubqueryParameterCount()).toArray(Integer[].class), true);
        return internalSetValues(ps, args, argTypes, startIndex);
    }

    private Pair<Object[], Integer[]> flatParameters() {
        final List<Object> arguments = Collects.emptyArrayList();
        final List<Integer> argumentTypes = Collects.emptyArrayList();
        Collects.forEach(this.args, new Consumer2<Integer, Object>() {
            @Override
            public void accept(Integer index, Object arg) {
                int type = argTypes[index];
                if (arg instanceof Collection && type != Types.ARRAY) {
                    Collection<?> entries = (Collection<?>) arg;
                    for (Object entry : entries) {
                        if (entry instanceof Object[]) {
                            Object[] valueArray = ((Object[]) entry);
                            for (Object argValue : valueArray) {
                                arguments.add(argValue);
                                argumentTypes.add(type);
                            }
                        } else {
                            arguments.add(entry);
                            argumentTypes.add(type);
                        }
                    }
                } else {
                    arguments.add(arg);
                    argumentTypes.add(type);
                }
            }
        });
        return new Entry<Object[], Integer[]>(Collects.toArray(arguments), Collects.toArray(argumentTypes, Integer[].class));
    }

    @Override
    public int setSubqueryParameters(PreparedStatement ps, QueryParameters queryParameters, int startIndex) throws SQLException {
        if (this.args != null) {
            Pair<Object[], Integer[]> parametersPair = flatParameters();
            Object[] args = Pipeline.of(parametersPair.getKey())
                    .limit(parametersPair.getKey().length - queryParameters.getAfterSubqueryParameterCount())
                    .skip(queryParameters.getBeforeSubqueryParameterCount())
                    .toArray();
            int[] argTypes = PrimitiveArrays.unwrap(
                    Pipeline.<Integer>of(parametersPair.getValue())
                            .limit(parametersPair.getValue().length - queryParameters.getAfterSubqueryParameterCount())
                            .skip(queryParameters.getBeforeSubqueryParameterCount())
                            .toArray(Integer[].class), true);
            return internalSetValues(ps, args, argTypes, startIndex);
        }
        return 0;
    }

    @Override
    public int setAfterSubqueryParameters(PreparedStatement ps, QueryParameters queryParameters, int startIndex) throws SQLException {
        Pair<Object[], Integer[]> parametersPair = flatParameters();
        Object[] args = Pipeline.of(parametersPair.getKey()).skip(queryParameters.getAfterSubqueryParameterCount()).toArray();
        int[] argTypes = PrimitiveArrays.unwrap(Pipeline.<Integer>of(parametersPair.getValue()).skip(queryParameters.getAfterSubqueryParameterCount()).toArray(Integer[].class), true);
        return internalSetValues(ps, args, argTypes, startIndex);
    }


    @Override
    public int setOriginalParameters(PreparedStatement ps, QueryParameters queryParameters, int startIndex) throws SQLException {
        return internalSetValues(ps, this.args, this.argTypes, 1);
    }


    @Override
    public void setValues(PreparedStatement ps) throws SQLException {
        internalSetValues(ps, this.args, this.argTypes, 1);
    }

    private int internalSetValues(PreparedStatement ps, Object[] args, int[] argTypes, int startIndex) throws SQLException {
        int count = 0;
        int parameterPosition = startIndex;
        if (args != null && argTypes != null) {
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
                                count++;
                            }
                        } else {
                            doSetValue(ps, parameterPosition, argTypes[i], entry);
                            parameterPosition++;
                            count++;
                        }
                    }
                } else {
                    doSetValue(ps, parameterPosition, argTypes[i], arg);
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
     *
     * @param ps                the PreparedStatement
     * @param parameterPosition index of the parameter position
     * @param argType           the argument type
     * @param argValue          the argument value
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

    public static class Factory {
        private static Field argsField;
        private static Field argTypesField;

        public static ArgumentTypePreparedStatementSetter create(org.springframework.jdbc.core.ArgumentTypePreparedStatementSetter setter) {
            if (argsField == null) {
                argsField = Reflects.getDeclaredField(org.springframework.jdbc.core.ArgumentTypePreparedStatementSetter.class, "args");
                if (argsField != null) {
                    argsField.setAccessible(true);
                }
            }
            if (argTypesField == null) {
                argTypesField = Reflects.getDeclaredField(org.springframework.jdbc.core.ArgumentTypePreparedStatementSetter.class, "argTypes");
                if (argTypesField != null) {
                    argTypesField.setAccessible(true);
                }
            }
            Object[] args = Reflects.getFieldValue(argsField, setter, true, false);
            int[] argTypes = Reflects.getFieldValue(argTypesField, setter, true, false);
            return new ArgumentTypePreparedStatementSetter(args, argTypes);
        }

    }

}
