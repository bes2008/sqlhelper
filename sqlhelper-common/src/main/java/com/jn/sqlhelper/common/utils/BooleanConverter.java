package com.jn.sqlhelper.common.utils;

import com.jn.langx.util.BooleanEvaluator;

public class BooleanConverter implements Converter<Object, Boolean> {
    private static final BooleanEvaluator booleanEvaluator = BooleanEvaluator.createFalseEvaluator(false, true, new Object[]{"false", 0, false, "off", 'n', "no"});

    @Override
    public Boolean apply(Object input) {
        return booleanEvaluator.evalTrue(input);
    }
}
