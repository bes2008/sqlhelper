package com.jn.sqlhelper.common.ddl.model.internal;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Predicate;

import java.sql.DatabaseMetaData;
import java.util.EnumSet;

public enum FkMutatedRule {
    importedKeyCascade(DatabaseMetaData.importedKeyCascade, "CASCADE"),
    importedKeyRestrict(DatabaseMetaData.importedKeyRestrict, "RESTRICT"),
    importedKeySetNull(DatabaseMetaData.importedKeySetNull, "SET NULL"),
    importedKeyNoAction(DatabaseMetaData.importedKeyNoAction, "NO ACTION"),
    importedKeySetDefault(DatabaseMetaData.importedKeySetDefault, "SET DEFAULT");

    private int code;
    private String keywords;

    FkMutatedRule(int code, String keywords) {
        this.code = code;
        this.keywords = keywords;
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

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
}
