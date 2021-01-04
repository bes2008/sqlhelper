package com.jn.sqlhelper.common.transaction;

import java.sql.SQLException;

public interface Transaction {
    TransactionDefinition getTransactionDefinition();

    TransactionManager getTransactionManager();

    void commit() throws SQLException;

    void rollback() throws SQLException;

    boolean isRollbackOnly();

    void setRollbackOnly();
}
