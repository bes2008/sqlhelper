package com.jn.sqlhelper.datasource.transaction;

import java.sql.SQLException;

public interface Transaction {
    void commit() throws SQLException;

    void rollback() throws SQLException;

    boolean isRollbackOnly();

    void setRollbackOnly();
}
