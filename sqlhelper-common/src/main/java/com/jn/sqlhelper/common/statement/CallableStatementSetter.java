package com.jn.sqlhelper.common.statement;

import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class CallableStatementSetter implements PreparedStatementSetter<List<?>> {

    @Override
    public void setParameters(PreparedStatement ps, int startIndex, List<?> parameters) throws SQLException {
        CallableStatement callableStatement = (CallableStatement) ps;
        for (int i = 0; i < parameters.size(); i++) {
            Object parameter = parameters.get(i);
            if (parameter instanceof CallableOutParameter) {
                CallableOutParameter outParameter = (CallableOutParameter) parameter;
                Integer scale = outParameter.getScale();
                if (scale == null) {
                    if (Strings.isBlank(outParameter.getName())) {
                        callableStatement.registerOutParameter(startIndex + i, outParameter.getSqlType());
                    } else {
                        callableStatement.registerOutParameter(outParameter.getName(), outParameter.getSqlType());
                    }
                } else {
                    if (Strings.isBlank(outParameter.getName())) {
                        callableStatement.registerOutParameter(startIndex + i, outParameter.getSqlType(), scale);
                    } else {
                        callableStatement.registerOutParameter(outParameter.getName(), outParameter.getSqlType(), scale);
                    }
                }
                if (outParameter.isInout()) {
                    callableStatement.setObject(startIndex + i, outParameter.getInValue());
                }
            } else {
                callableStatement.setObject(startIndex + i, parameters.get(i));
            }
        }
    }

}
