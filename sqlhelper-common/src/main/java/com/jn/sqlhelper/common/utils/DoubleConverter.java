package com.jn.sqlhelper.common.utils;

import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Numbers;
import com.jn.sqlhelper.common.exception.ValueConvertException;

public class DoubleConverter implements Converter<Object, Double> {
    public static final DoubleConverter INSTANCE = new DoubleConverter();
    @Override
    public Double apply(Object input) {
        if (input == null) {
            return 0D;
        }
        if (input instanceof Boolean) {
            return (Boolean) input ? 1D : 0D;
        }
        if (input instanceof Number) {
            return Numbers.convertNumberToTargetClass((Number) input, Double.class);
        }
        if (input instanceof String) {
            Number number = Numbers.createNumber(input.toString());
            return Numbers.convertNumberToTargetClass(number, Double.class);
        }
        throw new ValueConvertException(StringTemplates.formatWithPlaceholder("Can't cast {} to java.lang.Double", input));
    }
}
