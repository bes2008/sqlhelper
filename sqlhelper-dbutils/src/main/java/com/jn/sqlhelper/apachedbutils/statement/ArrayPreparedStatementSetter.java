package com.jn.sqlhelper.apachedbutils.statement;

import org.apache.commons.dbutils.OutParameter;

import java.sql.*;

public class ArrayPreparedStatementSetter implements PreparedStatementSetter {
    private Object[] params;

    public ArrayPreparedStatementSetter(Object... params) {
        this.params = params;
    }

    @Override
    public void setValues(PreparedStatement stmt) throws SQLException {
        // check the parameter count, if we can
        ParameterMetaData pmd = stmt.getParameterMetaData();

        CallableStatement call = null;
        if (stmt instanceof CallableStatement) {
            call = (CallableStatement) stmt;
        }

        boolean pmdKnownBroken = false;
        for (int i = 0; i < params.length; i++) {
            if (params[i] != null) {
                if (call != null && params[i] instanceof OutParameter) {
                    OutParameter parameter = (OutParameter) params[i];
                    call.registerOutParameter(i + 1, parameter.getSqlType());
                    if (parameter.getValue() != null) {
                        stmt.setObject(i + 1, parameter.getValue());
                    }
                } else {
                    stmt.setObject(i + 1, params[i]);
                }
            } else {
                // VARCHAR works with many drivers regardless
                // of the actual column type. Oddly, NULL and
                // OTHER don't work with Oracle's drivers.
                int sqlType = Types.VARCHAR;
                if (!pmdKnownBroken) {
                    try {
                        /*
                         * It's not possible for pmdKnownBroken to change from
                         * true to false, (once true, always true) so pmd cannot
                         * be null here.
                         */
                        sqlType = pmd.getParameterType(i + 1);
                    } catch (SQLException e) {
                        pmdKnownBroken = true;
                    }
                }
                stmt.setNull(i + 1, sqlType);
            }
        }
    }
}
