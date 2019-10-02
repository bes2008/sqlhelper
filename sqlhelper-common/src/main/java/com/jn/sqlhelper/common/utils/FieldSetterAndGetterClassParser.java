package com.jn.sqlhelper.common.utils;

import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.reflect.Modifiers;
import com.jn.langx.util.reflect.Reflects;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class FieldSetterAndGetterClassParser<F extends FieldInfo> implements ClassParser<Map<String, F>> {
    private boolean hierachial = false;
    private boolean zeroParameterConstructor = false;

    public FieldSetterAndGetterClassParser() {
    }

    public boolean isHierachial() {
        return hierachial;
    }

    public void setHierachial(boolean hierachial) {
        this.hierachial = hierachial;
    }

    public boolean isZeroParameterConstructor() {
        return zeroParameterConstructor;
    }

    public void setZeroParameterConstructor(boolean zeroParameterConstructor) {
        this.zeroParameterConstructor = zeroParameterConstructor;
    }

    private boolean isParsable(Class clazz) {
        if (clazz == null || clazz == Object.class || clazz.isInterface() || clazz.isArray() || clazz.isAnnotation() || clazz.isPrimitive()) {
            return false;
        }
        return true;
    }

    @Override
    public Map<String, F> parse(Class clazz) {
        return parse0(clazz, zeroParameterConstructor);
    }

    private Map<String, F> parse0(final Class clazz, boolean checkZeroParameterConstructor) {
        Preconditions.checkNotNull(clazz);
        Preconditions.checkTrue(!clazz.isInterface());
        Preconditions.checkTrue(Object.class != clazz);

        // zero parameter constructor

        final Map<String, F> fieldInfoMap = new HashMap<String, F>();
        Field[] fields = clazz.getDeclaredFields();
        Collects.forEach(fields, new Consumer<Field>() {
            @Override
            public void accept(Field field) {
                F fieldInfo = parseField(clazz, field);
                fieldInfoMap.put(field.getName(), fieldInfo);
            }
        });

        if (hierachial) {
            Class parentClass = clazz.getSuperclass();
            if (isParsable(parentClass)) {
                Collects.forEach(parse0(parentClass, false), new Consumer2<String, F>() {
                    @Override
                    public void accept(String fieldName, F fieldInfo) {
                        if (!fieldInfoMap.containsKey(fieldName)) {
                            fieldInfoMap.put(fieldName, fieldInfo);
                        }
                    }
                });
            }
        }
        return fieldInfoMap;
    }

    protected F parseField(Class clazz, Field field) {
        if (Modifiers.isStatic(field)) {
            return null;
        }

        FieldInfo fieldInfo = new FieldInfo();
        fieldInfo.setField(field);
        Class fieldType = field.getType();

        // setter
        Method setter = Reflects.getSetter(clazz, field.getName(), fieldType);

        // getter
        Method getter = Reflects.getGetter(clazz, field.getName());

        fieldInfo.setGetter(getter);

        fieldInfo.setSetter(setter);
        return (F) fieldInfo;
    }
}
