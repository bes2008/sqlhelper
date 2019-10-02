package com.jn.sqlhelper.common.utils;

import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Numbers;

public class IntegerConverter implements Converter<Object, Integer> {
    public static final IntegerConverter INSTANCE = new IntegerConverter();

    @Override
    public Integer apply(Object input) {
        if (input == null) {
            return 0;
        }
        if (input instanceof Boolean) {
            return (Boolean) input ? 1 : 0;
        }
        if (input instanceof Number) {
            return Numbers.convertNumberToTargetClass((Number) input, Integer.class);
        }
        if (input instanceof String) {
            Number number = Numbers.createNumber(input.toString());
            return Numbers.convertNumberToTargetClass(number, Integer.class);
        }
        throw new ClassCastException(StringTemplates.formatWithPlaceholder("Can't cast {} to java.lang.Integer", input));
    }
}
