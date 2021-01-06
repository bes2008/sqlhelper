/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-2.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jn.sqlhelper.common.transaction;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.invocation.MethodInvocation;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.reflect.Reflects;
import com.jn.sqlhelper.common.transaction.definition.TransactionDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionAops {
    private static final Logger logger = LoggerFactory.getLogger(TransactionAops.class);

    public static boolean willRollback(@NonNull Throwable ex, @NonNull TransactionDefinition definition) {
        return false;
    }


    public static Object invoke(@NonNull TransactionManager transactionManager, @NonNull TransactionDefinition definition, @NonNull MethodInvocation invocation) throws Throwable {
        Preconditions.checkNotNull(transactionManager, "the transaction manager is null");
        Preconditions.checkNotNull(definition, "the transaction definition is null");

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
            if (!nested) {
                if (transaction.isRollbackOnly()) {
                    // do log
                    logger.warn("will rollback a invocation with the transaction is marked as rollback-only: {} ", Reflects.getMethodString(invocation.getJoinPoint()));
                    transactionManager.rollback(transaction);
                } else {
                    transactionManager.commit(transaction);
                }
            }
            return ret;
        } catch (Throwable ex) {
            boolean rollback = transaction.isRollbackOnly();
            if (!rollback) {
                rollback = willRollback(ex, definition);
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
