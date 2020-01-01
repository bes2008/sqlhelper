package com.jn.sqlhelper.mybatis;

import org.apache.ibatis.mapping.BoundSql;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

class BoundSqls {
    private static Field additionalParametersField;

    static Map<String, Object> getAdditionalParameter(final BoundSql boundSql) {
        if (additionalParametersField != null) {
            try {
                return (Map<String, Object>) additionalParametersField.get(boundSql);
            } catch (IllegalAccessException ex) {
                // Noop
            }
        }
        return Collections.emptyMap();
    }

    static {
        try {
            (additionalParametersField = BoundSql.class.getDeclaredField("additionalParameters")).setAccessible(true);
        } catch (NoSuchFieldException ex) {
            // Noop
        }
    }
}
