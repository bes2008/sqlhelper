package com.jn.sqlhelper.common.resultset;

import com.jn.langx.util.Strings;
import com.jn.sqlhelper.common.utils.Converter;
import com.jn.sqlhelper.common.utils.FieldInfo;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class EntityFieldInfo extends FieldInfo {
    private final Set<String> columnNames = new LinkedHashSet<String>(); // one of columnNames
    private Converter converter;

    public void setField(Field field) {
        super.setField(field);
        columnNames.add(field.getName());
    }

    public Collection<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnName(String columnName) {
        if (Strings.isNotEmpty(columnName)) {
            this.columnNames.add(columnName);
        }
    }

    public void setColumnNames(Collection<String> columnNames) {
        this.columnNames.addAll(columnNames);
    }

    public Converter getConverter() {
        return converter;
    }

    public void setConverter(Converter converter) {
        this.converter = converter;
    }

    public static EntityFieldInfo of(FieldInfo fieldInfo) {
        if (fieldInfo == null) {
            return null;
        }
        EntityFieldInfo f = new EntityFieldInfo();

        f.setField(fieldInfo.getField());
        f.setSetter(fieldInfo.getSetter());
        f.setGetter(fieldInfo.getGetter());

        if (fieldInfo instanceof EntityFieldInfo) {
            EntityFieldInfo f0 = (EntityFieldInfo) fieldInfo;
            f.columnNames.addAll(f0.getColumnNames());
        }
        return f;
    }
}
