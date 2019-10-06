package com.jn.sqlhelper.common.ddlmodel.internal;

import com.jn.sqlhelper.common.utils.Converter;

public class JdbcTypeConverter implements Converter<Integer, JdbcType> {
    public static final JdbcTypeConverter INSTANCE = new JdbcTypeConverter();

    @Override
    public JdbcType apply(Integer input) {
        return JdbcType.ofCode(input);
    }
}
