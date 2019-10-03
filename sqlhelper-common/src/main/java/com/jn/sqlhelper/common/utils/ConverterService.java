package com.jn.sqlhelper.common.utils;

import com.jn.langx.text.StringTemplates;
import com.jn.sqlhelper.common.exception.ValueConvertException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConverterService {
    private static final Map<Class, Converter> BUILTIN = new HashMap<Class, Converter>();

    static {
        BUILTIN.put(Byte.class, ByteConverter.INSTANCE);
        BUILTIN.put(byte.class, ByteConverter.INSTANCE);
        BUILTIN.put(Short.class, ShortConverter.INSTANCE);
        BUILTIN.put(short.class, ShortConverter.INSTANCE);
        BUILTIN.put(Integer.class, IntegerConverter.INSTANCE);
        BUILTIN.put(int.class, IntegerConverter.INSTANCE);
        BUILTIN.put(Long.class, LongConverter.INSTANCE);
        BUILTIN.put(long.class, LongConverter.INSTANCE);
        BUILTIN.put(Float.class, FloatConverter.INSTANCE);
        BUILTIN.put(float.class, FloatConverter.INSTANCE);
        BUILTIN.put(Double.class, DoubleConverter.INSTANCE);
        BUILTIN.put(double.class, DoubleConverter.INSTANCE);
    }


    private final Map<Class, Converter> registry = new ConcurrentHashMap<Class, Converter>(BUILTIN);

    public static final ConverterService DEFAULT = new ConverterService();

    public void register(Class clazz, Converter converter) {
        registry.put(clazz, converter);
    }

    public <T> T convert(Object obj, Class<T> targetClass) {
        Converter converter = registry.get(targetClass);
        if (converter == null) {
            throw new ValueConvertException(StringTemplates.formatWithPlaceholder("Can't cast {} to {}", obj, targetClass));
        }
        return (T) converter.apply(obj);
    }

}
