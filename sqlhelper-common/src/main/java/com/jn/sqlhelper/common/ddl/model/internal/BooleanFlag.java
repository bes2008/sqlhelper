package com.jn.sqlhelper.common.ddl.model.internal;

import com.jn.langx.util.Strings;

public enum BooleanFlag {
    YES("YES"),
    NO("NO"),
    UNKNOWN("");

    private String str;

    BooleanFlag(String str) {
        this.str = str;
    }

    public static BooleanFlag of(String str) {
        if (Strings.isEmpty(str)) {
            return UNKNOWN;
        }
        return str.equalsIgnoreCase(YES.str) ? YES : NO;
    }
}
