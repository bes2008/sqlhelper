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

import java.sql.SQLException;
import java.util.Map;

public class DefaultTransactionManager implements TransactionManager {
    @Override
    public Transaction createTransaction(TransactionDefinition transactionDefinition) {
        return new Transaction(this, transactionDefinition);
    }

    @Override
    public void commit(Transaction transaction) throws SQLException {
        if (transaction.isRollbackOnly()) {
            rollback(transaction);
            return;
        }
        Map<Object, TransactionalResource> resourceMap = transaction.getResources();
        for (Map.Entry<Object, TransactionalResource> resourceEntry : resourceMap.entrySet()) {
            TransactionalResource resource = resourceEntry.getValue();
            resource.commit();
        }
    }

    @Override
    public void rollback(Transaction transaction) throws SQLException {
        Map<Object, TransactionalResource> resourceMap = transaction.getResources();
        for (Map.Entry<Object, TransactionalResource> resourceEntry : resourceMap.entrySet()) {
            TransactionalResource resource = resourceEntry.getValue();
            resource.rollback();
        }
    }
}
