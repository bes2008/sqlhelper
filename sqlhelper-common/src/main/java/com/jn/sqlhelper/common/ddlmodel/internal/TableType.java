package com.jn.sqlhelper.common.ddlmodel.internal;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Predicate;

import java.util.EnumSet;

public enum TableType {
    TABLE("TABLE"),
    VIEW("VIEW"),
    SYSTEM_TABLE("SYSTEM TABLE"),
    GLOBAL_TEMPORARY("GLOBAL TEMPORARY"),
    LOCAL_TEMPORARY("LOCAL TEMPORARY"),
    ALIAS("ALIAS"),
    SYNONYM("SYNONYM");

    private String code;

    TableType(String code) {
        this.code = code;
    }

    public static TableType ofCode(final String code) {
        return Collects.findFirst(EnumSet.allOf(TableType.class), new Predicate<TableType>() {
            @Override
            public boolean test(TableType value) {
                return value.code.equals(code);
            }
        });
    }

    public String getCode(){
        return code;
    }
}
