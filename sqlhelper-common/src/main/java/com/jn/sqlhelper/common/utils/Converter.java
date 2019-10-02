package com.jn.sqlhelper.common.utils;

public interface Converter<I, O> {
    O convert(I input, Class<O> type);
}
