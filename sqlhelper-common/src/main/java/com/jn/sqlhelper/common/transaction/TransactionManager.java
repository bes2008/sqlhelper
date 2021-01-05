package com.jn.sqlhelper.common.transaction;

import com.jn.sqlhelper.common.transaction.definition.TransactionDefinition;

import java.sql.SQLException;

public interface TransactionManager {
    Transaction createTransaction(TransactionDefinition transactionDefinition);

    void commit(Transaction transaction) throws SQLException;

    void rollback(Transaction transaction) throws SQLException;
}
