package com.jn.sqlhelper.common.ddl.model.internal;

import com.jn.langx.Converter;

public class FkInitiallyRuleConverter implements Converter<Integer, FkInitiallyRule> {
    @Override
    public FkInitiallyRule apply(Integer input) {
        return FkInitiallyRule.ofCode(input);
    }
}
