package com.jn.sqlhelper.common.ddlmodel.internal;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Predicate;

import java.sql.DatabaseMetaData;
import java.util.EnumSet;

public enum FkInitiallyRule {
    importedKeyInitiallyDeferred(DatabaseMetaData.importedKeyInitiallyDeferred),
    importedKeyInitiallyImmediate(DatabaseMetaData.importedKeyInitiallyImmediate),
    importedKeyNotDeferrable(DatabaseMetaData.importedKeyNotDeferrable);

    private int code;

    FkInitiallyRule(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static FkInitiallyRule ofCode(final int code) {
        return Collects.findFirst(EnumSet.allOf(FkInitiallyRule.class), new Predicate<FkInitiallyRule>() {
            @Override
            public boolean test(FkInitiallyRule r) {
                return r.code == code;
            }
        });
    }
}
