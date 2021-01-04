package com.jn.sqlhelper.common.transaction;

public abstract class AbstractTransaction implements Transaction{
    protected TransactionManager transactionManager;
    protected TransactionDefinition definition;
    protected boolean rollbackOnly = false;

    @Override
    public TransactionDefinition getTransactionDefinition() {
        return null;
    }

    @Override
    public TransactionManager getTransactionManager() {
        return null;
    }

    @Override
    public boolean isRollbackOnly() {
        return this.rollbackOnly;
    }

    @Override
    public void setRollbackOnly() {
        this.rollbackOnly =true;
    }
}
