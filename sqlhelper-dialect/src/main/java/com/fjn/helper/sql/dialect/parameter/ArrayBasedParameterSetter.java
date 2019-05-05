package com.fjn.helper.sql.dialect.parameter;

import com.fjn.helper.sql.dialect.PrepareParameterSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;


public class ArrayBasedParameterSetter implements PrepareParameterSetter<ArrayBasedQueryParameters> {
    @Override
    public int setParameters(PreparedStatement statement, ArrayBasedQueryParameters parameters, int startIndex)
            throws SQLException {
        if (parameters.getParameterValuesSize() > 0) {
            for (Object value : (Object[]) parameters.getParameterValues()) {
                statement.setObject(startIndex, value);
                startIndex++;
            }
        }
        return startIndex;
    }
}