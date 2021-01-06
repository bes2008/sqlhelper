package com.jn.sqlhelper.datasource.spring.aop;

import com.jn.agileway.aop.adapter.aopalliance.MethodInvocationAdapter;
import com.jn.sqlhelper.common.transaction.TransactionAops;
import com.jn.sqlhelper.common.transaction.TransactionManager;
import com.jn.sqlhelper.common.transaction.definition.TransactionDefinition;
import com.jn.sqlhelper.common.transaction.definition.TransactionDefinitionRegistry;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalizeGlobalTransactionInterceptor implements MethodInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LocalizeGlobalTransactionInterceptor.class);
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
        if (transactionManager == null || definitionRegistry == null) {
            return invocation.proceed();
        }

        TransactionDefinition definition = definitionRegistry.get(invocation.getMethod());
        if (definition == null) {
            return invocation.proceed();
        }

        MethodInvocationAdapter adapter = new MethodInvocationAdapter(invocation);
        return TransactionAops.invoke(transactionManager, definition, adapter);
    }
}
