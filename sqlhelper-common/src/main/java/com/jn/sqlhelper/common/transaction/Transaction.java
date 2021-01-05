package com.jn.sqlhelper.common.transaction;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.multivalue.CommonMultiValueMap;
import com.jn.langx.util.collection.multivalue.MultiValueMap;
import com.jn.langx.util.function.Supplier;

import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * <pre>
 * 1. 该对象将被放到 ThreadLocal中
 * 2. 该对象只能由TransactionManager来创建
 * </pre>
 */
public class Transaction {
    private TransactionManager transactionManager;
    private TransactionDefinition definition;
    private boolean rollbackOnly = false;
    private final MultiValueMap<Object, TransactionalResource> resources = new CommonMultiValueMap<Object, TransactionalResource>(new LinkedHashMap<Object, Collection<TransactionalResource>>(), new Supplier<Object, Collection<TransactionalResource>>() {
        @Override
        public Collection<TransactionalResource> get(Object key) {
            return Collects.emptyHashSet(true);
        }
    });

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
        this.resources.add(key, transactionalResource);
    }

    public boolean hasResource(Object key) {
        if (key == null) {
            return false;
        }
        return resources.containsKey(key);
    }

    public void clearResource(Object key) {
        if (key != null) {
            resources.remove(key);
        }
    }

    public void clearResources() {
        resources.clear();
    }

    public MultiValueMap<Object, TransactionalResource> getResources() {
        return this.resources;
    }

    @Override
    public String toString() {
        return this.definition.toString();
    }
}
