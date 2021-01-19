package com.jn.sqlhelper.common.transaction;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <pre>
 * 1. 该对象将被放到 ThreadLocal中
 * 2. 该对象只能由TransactionManager来创建
 * </pre>
 */
public class Transaction {
    private TransactionManager transactionManager;
    private TransactionDefinition definition;
    /**
     * https://zhuanlan.zhihu.com/p/32720499
     * <p>
     * 通常在内层事务中，来标记全局事务是否要rollback
     */
    private boolean rollbackOnly = false;
    private final Map<Object, TransactionalResource> resources = new LinkedHashMap<Object, TransactionalResource>();

    public Transaction(TransactionManager transactionManager, TransactionDefinition definition) {
        this.setDefinition(definition);
        this.setTransactionManager(transactionManager);
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setDefinition(TransactionDefinition definition) {
        this.definition = definition;
    }

    public TransactionDefinition getTransactionDefinition() {
        return this.definition;
    }

    public TransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    public boolean isRollbackOnly() {
        return this.rollbackOnly;
    }

    public void setRollbackOnly() {
        this.rollbackOnly = true;
    }

    public void bindResource(Object key, TransactionalResource transactionalResource) {
        if (key == null) {
            return;
        }
        this.resources.put(key, transactionalResource);
    }

    public boolean hasResource(Object key) {
        if (key == null) {
            return false;
        }
        return resources.containsKey(key);
    }

    public void unbindResource(Object key) {
        if (key != null) {
            resources.remove(key);
        }
    }

    public void clearResources() {
        resources.clear();
    }

    public Map<Object, TransactionalResource> getResources() {
        return this.resources;
    }

    public TransactionalResource getResource(Object key) {
        return this.resources.get(key);
    }

    @Override
    public String toString() {
        return this.definition.toString();
    }
}
