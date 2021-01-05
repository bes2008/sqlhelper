package com.jn.sqlhelper.common.transaction;

import com.jn.langx.Named;

public interface TransactionDefinition extends Named {
    int getIsolationLevel();

    boolean isReadonly();
}
