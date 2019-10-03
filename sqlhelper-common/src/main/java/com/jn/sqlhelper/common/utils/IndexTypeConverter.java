package com.jn.sqlhelper.common.utils;

public class IndexTypeConverter implements Converter<Integer, IndexType> {
    @Override
    public IndexType apply(Integer input) {
        return IndexType.ofCode(input);
    }
}
