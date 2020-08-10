package com.jn.sqlhelper.common.ddl.model.internal;

import com.jn.langx.Converter;

public class FkMutatedRuleConverter implements Converter<Integer, FkMutatedRule> {

    public boolean isConvertible(Class sourceClass, Class targetClass) {
        return sourceClass==Integer.class || sourceClass==Integer.TYPE;
    }

    @Override
    public FkMutatedRule apply(Integer input) {
        return FkMutatedRule.ofCode(input);
    }
}
