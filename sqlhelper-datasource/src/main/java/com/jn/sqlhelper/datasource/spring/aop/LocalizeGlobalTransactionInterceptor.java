package com.jn.sqlhelper.datasource.spring.aop;

import com.jn.sqlhelper.common.transaction.TransactionManager;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class LocalizeGlobalTransactionInterceptor implements MethodInterceptor {

    private TransactionManager transactionManager;

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // 暂时全部开启事务
        //TransactionDefinition definition = new
        //transactionManager.createTransaction()

        return invocation.proceed();
    }
}
