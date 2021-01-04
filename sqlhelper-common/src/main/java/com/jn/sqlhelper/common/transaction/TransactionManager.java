package com.jn.sqlhelper.common.transaction;

public interface TransactionManager {
    Transaction createTransaction(TransactionDefinition transactionDefinition);

    Transaction getCurrentTransaction();

    void commit(Transaction transaction);

    void rollback(Transaction transaction);
}
