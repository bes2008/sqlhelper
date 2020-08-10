package com.jn.sqlhelper.common.ddl.model.internal;

import com.jn.langx.Converter;

public class JdbcTypeConverter implements Converter<Integer, JdbcType> {
    public static final JdbcTypeConverter INSTANCE = new JdbcTypeConverter();

    public boolean isConvertible(Class sourceClass, Class targetClass) {
        return sourceClass == Integer.TYPE || sourceClass == Integer.class;
    }

    @Override
    public JdbcType apply(Integer input) {
        return JdbcType.ofCode(input);
    }
}
