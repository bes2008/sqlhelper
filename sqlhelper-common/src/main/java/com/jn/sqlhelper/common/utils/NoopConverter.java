package com.jn.sqlhelper.common.utils;

public class NoopConverter implements Converter {
    public static final NoopConverter INSTANCE = new NoopConverter();
    @Override
    public Object apply(Object input) {
        return input;
    }
}
