package com.jn.sqlhelper.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public class FieldInfo {
    private Field field;
    private Method getter;
    private Method setter;

    private Map<String, Object> props;

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

    public Map<String, Object> getProps() {
        return props;
    }

    public void setProps(Map<String, Object> props) {
        this.props = props;
    }
}
