package com.jn.sqlhelper.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class FieldInfo {
    private Field field;
    private Method getter;
    private Method setter;

    public String getFieldName(){
        return field.getName();
    }

    public Class getFieldType(){
        return field.getType();
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Method getGetter() {
        return getter;
    }

    public void setGetter(Method getter) {
        this.getter = getter;
    }

    public Method getSetter() {
        return setter;
    }

    public void setSetter(Method setter) {
        this.setter = setter;
    }

}
