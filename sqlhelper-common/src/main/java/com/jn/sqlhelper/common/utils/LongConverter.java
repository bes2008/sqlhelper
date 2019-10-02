package com.jn.sqlhelper.common.utils;

import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Numbers;

import java.util.Date;

public class LongConverter implements Converter<Object, Long> {
    @Override
    public Long apply(Object input) {
        if (input == null) {
            return 0L;
        }
        if (input instanceof Boolean) {
            return (Boolean) input ? 1L : 0L;
        }
        if (input instanceof Number) {
            return Numbers.convertNumberToTargetClass((Number) input, Long.class);
        }
        if (input instanceof String) {
            Number number = Numbers.createNumber(input.toString());
            return Numbers.convertNumberToTargetClass(number, Long.class);
        }

        if(input instanceof Date){
            return ((Date) input).getTime();
        }
        throw new ClassCastException(StringTemplates.formatWithPlaceholder("Can't cast {} to java.lang.Long", input));
    }
}
