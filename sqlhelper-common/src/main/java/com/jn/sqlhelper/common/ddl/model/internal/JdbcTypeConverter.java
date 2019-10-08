package com.jn.sqlhelper.common.ddl.model.internal;

import com.jn.langx.Converter;

public class JdbcTypeConverter implements Converter<Integer, JdbcType> {
    public static final JdbcTypeConverter INSTANCE = new JdbcTypeConverter();

    @Override
    public JdbcType apply(Integer input) {
        return JdbcType.ofCode(input);
    }
}
