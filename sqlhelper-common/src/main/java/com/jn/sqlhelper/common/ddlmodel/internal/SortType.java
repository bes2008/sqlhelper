package com.jn.sqlhelper.common.ddlmodel.internal;

import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Predicate;

import java.util.EnumSet;

public enum SortType {
    ASC(new String[]{"A"}),
    DESC(new String[]{"D"}),
    UNSUPPORTED;
    private String[] maybeValues = {};

    SortType() {
    }

    SortType(String[] maybeValues) {
        this.maybeValues = maybeValues;
    }

    public static SortType ofCode(final String code) {
        if (Strings.isEmpty(code)) {
            return UNSUPPORTED;
        }
        SortType sortType = Collects.findFirst(EnumSet.allOf(SortType.class), new Predicate<SortType>() {
            @Override
            public boolean test(SortType value) {
                return Collects.asList(value.maybeValues).contains(code);
            }
        });
        if (sortType == null) {
            return UNSUPPORTED;
        }
        return sortType;
    }
}
