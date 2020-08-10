package com.jn.sqlhelper.common.ddl.model.internal;

import com.jn.langx.Converter;

public class SortTypeConverter implements Converter<String, SortType> {

    public boolean isConvertible(Class sourceClass, Class targetClass) {
        return String.class == sourceClass;
    }

    @Override
    public SortType apply(String input) {
        return SortType.ofCode(input);
    }
}
