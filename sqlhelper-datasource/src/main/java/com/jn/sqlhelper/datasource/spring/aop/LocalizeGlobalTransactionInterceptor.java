package com.jn.sqlhelper.datasource.spring.aop;

import com.jn.langx.annotation.Nullable;
import com.jn.sqlhelper.common.transaction.Transaction;
import com.jn.sqlhelper.common.transaction.TransactionManager;
import com.jn.sqlhelper.common.transaction.Transactions;
import com.jn.sqlhelper.common.transaction.definition.TransactionDefinition;
import com.jn.sqlhelper.common.transaction.definition.TransactionDefinitionRegistry;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class LocalizeGlobalTransactionInterceptor implements MethodInterceptor {

    private TransactionManager transactionManager;
    private TransactionDefinitionRegistry definitionRegistry;

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setDefinitionRegistry(TransactionDefinitionRegistry definitionRegistry) {
        this.definitionRegistry = definitionRegistry;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        @Nullable
        TransactionDefinition definition = definitionRegistry.get(invocation.getMethod());
        // 当前方法没有指定事务
        if (definition == null) {
            return invocation.proceed();
        }

        Transaction transaction = Transactions.get();

        // 是否嵌入在一个已有事务内部
        boolean nested = transaction != null;

        // 定义了事务
        if (!nested) {
            transaction = transactionManager.createTransaction(definition);
        }
        Transactions.bind(transaction);

        try {
            Object ret = invocation.proceed();
            // 接下来是要提交事务了

            // 但是发现了内层事务被回滚了。
            if (transaction.isRollbackOnly()) {
                transactionManager.rollback(transaction);
            } else {
                transactionManager.commit(transaction);
            }
            return ret;
        } catch (Throwable ex) {
            boolean rollback = transaction.isRollbackOnly();
            if (!rollback) {
                rollback = Transactions.willRollback(ex, definition);
            }
            if (!rollback) {
                if (nested) {
                    // log it
                } else {
                    transactionManager.commit(transaction);
                }
            } else {
                if (nested) {
                    // 标记内层事务被回滚了
                    transaction.setRollbackOnly();
                }
                transactionManager.rollback(transaction);
            }
            return ex;
        } finally {
            if (!nested) {
                transaction.clearResources();
                Transactions.unbind();
            }
        }
    }
}
