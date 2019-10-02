package com.jn.sqlhelper.common.resultset;

import com.jn.langx.util.Strings;
import com.jn.sqlhelper.common.utils.FieldInfo;

import java.lang.reflect.Field;

public class EntityFieldInfo extends FieldInfo {
    private String columnName;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
        if(Strings.isEmpty(columnName)){
            this.columnName = getField().getName();
        }
    }

    public static EntityFieldInfo of(FieldInfo fieldInfo){
        if(fieldInfo==null){
            return null;
        }
        EntityFieldInfo f = new EntityFieldInfo();

        f.setField(fieldInfo.getField());
        f.setSetter(fieldInfo.getSetter());
        f.setGetter(fieldInfo.getGetter());

        if(fieldInfo instanceof EntityFieldInfo){
            EntityFieldInfo f0 = (EntityFieldInfo)fieldInfo;
            f.setColumnName(f0.getColumnName());
        }
        return f;
    }
}
