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

import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.enums.Enums;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Transactions {
    private Transactions() {
    }

    private static final Logger logger = LoggerFactory.getLogger(Transactions.class);

    public static final int getTransactionIsolationLevel(@Nullable String transactionIsolationName) {
        Isolation isolation = getTransactionIsolation(transactionIsolationName);
        return isolation.getCode();
    }

    public static final Isolation getTransactionIsolation(@Nullable String transactionIsolationName) {
        return getTransactionIsolation(transactionIsolationName, null);
    }

    public static final Isolation getTransactionIsolation(@Nullable String transactionIsolationName, @Nullable Isolation ifNull) {
        Isolation isolation = null;
        if (Emptys.isNotEmpty(transactionIsolationName)) {
            isolation = Enums.ofDisplayText(Isolation.class, transactionIsolationName);
            if (isolation == null) {
                isolation = Enums.ofName(Isolation.class, transactionIsolationName);
            }
        }

        if (isolation == null) {
            logger.warn("the transactionIsolationName is invalid: {}", transactionIsolationName);
            isolation = ifNull;
        }
        return isolation;
    }

}
