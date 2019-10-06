package com.jn.sqlhelper.common.ddlmodel.internal;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Predicate;

import java.util.EnumSet;

public enum IndexType {
    tableIndexStatistic(0),
    tableIndexClustered(1),
    tableIndexHashed(2),
    tableIndexOther(3);

    private int code;

    IndexType(int code) {
        this.code = code;
    }

    public static IndexType ofCode(final int code) {
        return Collects.findFirst(EnumSet.allOf(IndexType.class), new Predicate<IndexType>() {
            @Override
            public boolean test(IndexType value) {
                return value.code == code;
            }
        });
    }
}
