package com.jn.sqlhelper.common.resultset;

import com.jn.langx.Converter;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.converter.NoopConverter;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.reflect.classparse.FieldSetterAndGetterClassParser;
import com.jn.sqlhelper.common.annotation.Column;

import java.lang.reflect.Field;

public class EntityBeanClassParser extends FieldSetterAndGetterClassParser<EntityFieldInfo> {

    public EntityBeanClassParser() {
        setHierachial(true);
        setZeroParameterConstructor(true);
    }

    @Override
    protected EntityFieldInfo parseField(Class clazz, Field field) {
        EntityFieldInfo fieldInfo = EntityFieldInfo.of(super.parseField(clazz, field));
        if (fieldInfo != null) {
            Column column = Reflects.getDeclaredAnnotation(field, Column.class);
            if (column != null && Emptys.isNotEmpty(column.value())) {
                fieldInfo.setColumnNames(Collects.asList(column.value()));
                Class converterClass = column.converter();
                if (converterClass != null && converterClass != NoopConverter.class) {
                    Converter converter = Reflects.<Converter>newInstance(converterClass);
                    fieldInfo.setConverter(converter);
                }
            }
        }
        return fieldInfo;
    }
}
