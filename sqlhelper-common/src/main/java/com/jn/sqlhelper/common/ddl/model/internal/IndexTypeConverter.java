package com.jn.sqlhelper.common.ddl.model.internal;

import com.jn.langx.Converter;

public class IndexTypeConverter implements Converter<Integer, IndexType> {

    public boolean isConvertible(Class sourceClass, Class targetClass) {
        return sourceClass == Integer.class || sourceClass == Integer.TYPE;
    }

    @Override
    public IndexType apply(Integer input) {
        return IndexType.ofCode(input);
    }
}
