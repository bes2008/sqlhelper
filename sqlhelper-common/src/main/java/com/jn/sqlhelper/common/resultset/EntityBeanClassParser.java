package com.jn.sqlhelper.common.resultset;

import com.jn.langx.util.reflect.Reflects;
import com.jn.sqlhelper.common.annotation.Column;
import com.jn.sqlhelper.common.utils.FieldSetterAndGetterClassParser;

import java.lang.reflect.Field;

public class EntityBeanClassParser extends FieldSetterAndGetterClassParser<EntityFieldInfo> {
    public static final String COLUMN_NAME = "COLUMN_NAME";

    public EntityBeanClassParser() {
        setHierachial(true);
        setZeroParameterConstructor(true);
    }

    @Override
    protected EntityFieldInfo parseField(Class clazz, Field field) {
        EntityFieldInfo fieldInfo = EntityFieldInfo.of(super.parseField(clazz, field));
        if (fieldInfo != null) {
            Column column = Reflects.getDeclaredAnnotation(field, Column.class);
            if (column != null) {
                fieldInfo.setColumnName(column.value());
            }
        }
        return fieldInfo;
    }
}
