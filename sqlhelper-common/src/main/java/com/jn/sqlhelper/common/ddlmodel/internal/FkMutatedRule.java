package com.jn.sqlhelper.common.ddlmodel.internal;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Predicate;

import java.sql.DatabaseMetaData;
import java.util.EnumSet;

public enum FkMutatedRule {
    importedKeyCascade(DatabaseMetaData.importedKeyCascade),
    importedKeyRestrict(DatabaseMetaData.importedKeyRestrict),
    importedKeySetNull(DatabaseMetaData.importedKeySetNull),
    importedKeyNoAction(DatabaseMetaData.importedKeyNoAction),
    importedKeySetDefault(DatabaseMetaData.importedKeySetDefault);

    private int code;

    FkMutatedRule(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static FkMutatedRule ofCode(final int code) {
        return Collects.findFirst(EnumSet.allOf(FkMutatedRule.class), new Predicate<FkMutatedRule>() {
            @Override
            public boolean test(FkMutatedRule r) {
                return r.code == code;
            }
        });
    }
}
