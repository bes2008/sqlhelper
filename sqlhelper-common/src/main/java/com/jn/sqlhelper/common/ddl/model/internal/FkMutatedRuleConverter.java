package com.jn.sqlhelper.common.ddl.model.internal;

import com.jn.langx.Converter;

public class FkMutatedRuleConverter implements Converter<Integer, FkMutatedRule> {
    @Override
    public FkMutatedRule apply(Integer input) {
        return FkMutatedRule.ofCode(input);
    }
}
