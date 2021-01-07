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

package com.jn.sqlhelper.common.transaction.utils;

import com.jn.langx.util.struct.ThreadLocalHolder;
import com.jn.sqlhelper.common.transaction.Transaction;
import com.jn.sqlhelper.common.transaction.TransactionalResource;

public class TransactionThreadContext {
    private TransactionThreadContext(){}
    private static final ThreadLocalHolder<Transaction> TRANSACTION_HOLDER = new ThreadLocalHolder<Transaction>();

    public static void bind(Transaction transaction) {
        TRANSACTION_HOLDER.set(transaction);
    }

    public static Transaction get() {
        return TRANSACTION_HOLDER.get();
    }

    public static Transaction unbind() {
        Transaction transaction = get();
        TRANSACTION_HOLDER.reset();
        return transaction;
    }

    public static boolean bindTransactionResource(Object key, TransactionalResource resource) {
        Transaction transaction = get();
        if (transaction == null) {
            return false;
        }
        transaction.bindResource(key, resource);
        return true;
    }

    public static boolean unbindTransactionResource(Object key) {
        Transaction transaction = get();
        if (transaction == null) {
            return false;
        }
        transaction.unbindResource(key);
        return true;
    }

}
