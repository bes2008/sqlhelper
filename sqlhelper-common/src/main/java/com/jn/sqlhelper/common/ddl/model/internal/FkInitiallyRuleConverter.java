package com.jn.sqlhelper.common.ddl.model.internal;

import com.jn.langx.Converter;

public class FkInitiallyRuleConverter implements Converter<Integer, FkInitiallyRule> {

    public boolean isConvertible(Class sourceClass, Class targetClass) {
        return sourceClass==Integer.class || sourceClass ==Integer.TYPE;
    }

    @Override
    public FkInitiallyRule apply(Integer input) {
        return FkInitiallyRule.ofCode(input);
    }
}
