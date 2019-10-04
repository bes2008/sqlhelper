package com.jn.sqlhelper.common.resultset;

import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.reflect.Modifiers;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.reflect.type.Primitives;
import com.jn.sqlhelper.common.exception.NoMappedFieldException;
import com.jn.sqlhelper.common.exception.ValueConvertException;
import com.jn.sqlhelper.common.symbolmapper.SqlSymbolMapper;
import com.jn.sqlhelper.common.symbolmapper.UnderlineToCamelSymbolMapper;
import com.jn.sqlhelper.common.utils.Converter;
import com.jn.sqlhelper.common.utils.ConverterService;
import com.jn.sqlhelper.common.utils.FieldInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.Map;

public class BeanRowMapper<T> implements RowMapper<T> {
    private static final Logger logger = LoggerFactory.getLogger(BeanRowMapper.class);

    private Class<T> targetClass; // map an row to an instance of the class
    private ConverterService converterService = ConverterService.DEFAULT; // value converter
    private SqlSymbolMapper sqlSymbolMapper; // for guess field by column name

    public BeanRowMapper(Class<T> beanClass) {
        this(beanClass, true);
    }

    public BeanRowMapper(Class<T> beanClass, boolean useCache) {
        Preconditions.checkNotNull(beanClass);
        this.targetClass = beanClass;
        this.fieldMap = (useCache ? CachedEntityBeanClassParser.getInstance() : new EntityBeanClassParser()).parse(targetClass);
    }

    private Map<String, EntityFieldInfo> fieldMap;

    @Override
    public T mapping(ResultSet row, int currentRowIndex, ResultSetDescription resultSetDescription) {
        if (sqlSymbolMapper == null) {
            sqlSymbolMapper = new UnderlineToCamelSymbolMapper();
        }
        int columnCount = resultSetDescription.getColumnCount();
        T instance = Reflects.newInstance(targetClass);
        for (int i = 1; i <= columnCount; i++) {
            String columnName = resultSetDescription.getColumnName(i);
            EntityFieldInfo fieldInfo = findFieldForColumn(columnName);
            if (fieldInfo == null) {
                String errorMessage = StringTemplates.formatWithPlaceholder("Can't find a field link to a column: {} in the class: {}", columnName, targetClass);
                throw new NoMappedFieldException(errorMessage);
            }
            Object value = null;
            try {
                value = ResultSets.getResultSetValue(row, i, fieldInfo.getFieldType());
            } catch (Throwable ex) {
                try {
                    value = ResultSets.getResultSetValue(row, i);
                } catch (Throwable ex2) {
                    throw Throwables.wrapAsRuntimeException(ex2);
                }
            }


            // convert value
            if (value != null && !Primitives.wrap(fieldInfo.getFieldType()).isAssignableFrom(value.getClass())) {
                Converter converter = fieldInfo.getConverter();
                if (converter != null) {
                    try {
                        value = converter.apply(value);
                    } catch (Exception ex) {
                        logger.warn(ex.getMessage(), ex);
                        throw new ValueConvertException(StringTemplates.formatWithPlaceholder("Can't convert {} to {} for {}#{}", value.getClass(), fieldInfo.getFieldType(), Reflects.getFQNClassName(targetClass), fieldInfo.getFieldName()));
                    }
                }

            }
            if (value != null && !Primitives.wrap(fieldInfo.getFieldType()).isAssignableFrom(value.getClass())) {

                if (converterService != null) {
                    try {
                        value = converterService.convert(value, fieldInfo.getFieldType());
                    } catch (Throwable ex) {
                        logger.warn(ex.getMessage(), ex);
                        throw new ValueConvertException(StringTemplates.formatWithPlaceholder("Can't convert {} to {} for {}#{}", value.getClass(), fieldInfo.getFieldType(), Reflects.getFQNClassName(targetClass), fieldInfo.getFieldName()));
                    }
                }
            }

            if (value != null && !Primitives.wrap(fieldInfo.getFieldType()).isAssignableFrom(value.getClass())) {
                throw new ValueConvertException(StringTemplates.formatWithPlaceholder("Can't convert {} to {} for {}#{}", value.getClass(), fieldInfo.getFieldType(), Reflects.getFQNClassName(targetClass), fieldInfo.getFieldName()));
            }
            // set value
            try {
                setValue(fieldInfo, instance, value);
            } catch (Throwable ex) {
                throw Throwables.wrapAsRuntimeException(ex);
            }

        }
        return instance;
    }


    private EntityFieldInfo findFieldForColumn(final String columnName) {
        if (fieldMap == null) {
            return null;
        }

        EntityFieldInfo fieldInfo = fieldMap.get(columnName);
        if (fieldInfo != null) {
            return fieldInfo;
        }

        fieldInfo = Collects.findFirst(fieldMap.values(), new Predicate<EntityFieldInfo>() {
            @Override
            public boolean test(EntityFieldInfo field) {
                if (field.getFieldName().equalsIgnoreCase(columnName) || field.getColumnName().equalsIgnoreCase(columnName)) {
                    return true;
                }
                if (sqlSymbolMapper != null) {
                    return sqlSymbolMapper.apply(columnName).equalsIgnoreCase(sqlSymbolMapper.apply(field.getFieldName()));
                }
                return false;
            }
        });
        if (fieldInfo != null) {
            fieldMap.put(columnName, fieldInfo);
        }
        return fieldInfo;
    }

    private void setValue(FieldInfo fieldInfo, Object target, Object fieldValue) throws Throwable {
        Method method = fieldInfo.getSetter();
        if (method != null && Modifiers.isPublic(method)) {
            method.setAccessible(true);
            method.invoke(target, fieldValue);
        } else {
            fieldInfo.getField().setAccessible(true);
            fieldInfo.getField().set(target, fieldValue);
        }
    }

    public ConverterService getConverterService() {
        return converterService;
    }

    public void setConverterService(ConverterService converterService) {
        this.converterService = converterService;
    }

    public void setSqlSymbolMapper(SqlSymbolMapper sqlSymbolMapper) {
        this.sqlSymbolMapper = sqlSymbolMapper;
    }
}
