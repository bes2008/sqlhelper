package com.jn.sqlhelper.common.transaction;

import java.sql.SQLException;

/**
 * @see com.jn.sqlhelper.common.transaction.utils.TransactionAops
 * @since 3.4.3
 */
public interface TransactionManager {
    Transaction createTransaction(TransactionDefinition transactionDefinition);

    void commit(Transaction transaction) throws SQLException;

    void rollback(Transaction transaction) throws SQLException;
}
