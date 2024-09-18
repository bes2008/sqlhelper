package com.jn.sqlhelper.dialect.internal;

import java.sql.CallableStatement;
import java.sql.SQLException;

/**
 * https://docs.microsoft.com/zh-cn/sql/t-sql/queries/select-transact-sql?view=sql-server-ver15
 */
public abstract class AbstractTransactSQLDialect extends AbstractDialect {
    @Override
    public int registerResultSetOutParameter(CallableStatement statement, int col)
            throws SQLException {
        return col;
    }
}
