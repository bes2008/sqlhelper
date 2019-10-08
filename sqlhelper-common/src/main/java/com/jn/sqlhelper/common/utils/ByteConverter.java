package com.jn.sqlhelper.common.utils;

import com.jn.langx.util.Numbers;

public class ByteConverter implements Converter<Object, Byte> {
    public static final ByteConverter INSTANCE = new ByteConverter();
    private static final IntegerConverter integerConverter = IntegerConverter.INSTANCE;

    @Override
    public Byte apply(Object input) {
        Integer integer = integerConverter.apply(input);
        return Numbers.convertNumberToTargetClass(integer, Byte.class);
    }
}
