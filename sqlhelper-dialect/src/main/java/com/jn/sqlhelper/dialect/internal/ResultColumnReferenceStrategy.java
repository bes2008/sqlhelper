package com.jn.sqlhelper.dialect.internal;


public enum ResultColumnReferenceStrategy {
    SOURCE,
    ALIAS,
    ORDINAL;

    private ResultColumnReferenceStrategy() {
    }


    public static ResultColumnReferenceStrategy resolveByName(String name) {
        if (ALIAS.name().equalsIgnoreCase(name)) {
            return ALIAS;
        }
        if (ORDINAL.name().equalsIgnoreCase(name)) {
            return ORDINAL;
        }
        return SOURCE;
    }
}
