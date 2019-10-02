package com.jn.sqlhelper.common.utils;

import com.jn.langx.util.Numbers;

public class ShortConverter implements Converter<Object, Short> {
    private IntegerConverter integerConverter = IntegerConverter.INSTANCE;

    @Override
    public Short apply(Object input) {
        Integer integer = integerConverter.apply(input);
        return Numbers.convertNumberToTargetClass(integer, Short.class);
    }
}
