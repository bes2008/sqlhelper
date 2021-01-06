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
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.enums.Enums;
import com.jn.langx.util.struct.ThreadLocalHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Transactions {
    private Transactions() {
    }

    private static final Logger logger = LoggerFactory.getLogger(Transactions.class);

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

    public static final int getTransactionIsolationLevel(@NonNull String transactionIsolationName) {
        Isolation isolation = getTransactionIsolation(transactionIsolationName);
        return isolation.getCode();
    }

    public static final Isolation getTransactionIsolation(@NonNull String transactionIsolationName) {
        return getTransactionIsolation(transactionIsolationName, Isolation.DEFAULT);
    }

    public static final Isolation getTransactionIsolation(@NonNull String transactionIsolationName, @Nullable Isolation ifNull) {
        Preconditions.checkNotEmpty(transactionIsolationName, "the transaction isolation level name is null or empty");
        Isolation isolation = Enums.ofDisplayText(Isolation.class, transactionIsolationName);
        if (isolation == null || isolation == Isolation.DEFAULT) {
            isolation = Enums.ofName(Isolation.class, transactionIsolationName);
        }
        if (isolation == null) {
            logger.warn("the transactionIsolationName is invalid: {}", transactionIsolationName);
            isolation = ifNull;
        }
        return isolation;
    }

}
