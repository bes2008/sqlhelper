package com.fjn.helper.sql.dialect.internal;

import java.sql.CallableStatement;
import java.sql.SQLException;


public abstract class AbstractTransactSQLDialect extends AbstractDialect {
    @Override
    public int registerResultSetOutParameter(CallableStatement statement, int col)
            throws SQLException {
        return col;
    }
}
