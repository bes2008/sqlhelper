package com.jn.sqlhelper.common.utils;

public class SortTypeConverter implements Converter<String, SortType> {
    @Override
    public SortType apply(String input) {
        return SortType.ofCode(input);
    }
}
