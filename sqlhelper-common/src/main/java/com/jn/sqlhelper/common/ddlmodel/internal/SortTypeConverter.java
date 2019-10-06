package com.jn.sqlhelper.common.ddlmodel.internal;

import com.jn.sqlhelper.common.utils.Converter;

public class SortTypeConverter implements Converter<String, SortType> {
    @Override
    public SortType apply(String input) {
        return SortType.ofCode(input);
    }
}
