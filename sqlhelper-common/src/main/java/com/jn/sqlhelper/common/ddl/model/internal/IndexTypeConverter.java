package com.jn.sqlhelper.common.ddl.model.internal;

import com.jn.langx.Converter;

public class IndexTypeConverter implements Converter<Integer, IndexType> {
    @Override
    public IndexType apply(Integer input) {
        return IndexType.ofCode(input);
    }
}
