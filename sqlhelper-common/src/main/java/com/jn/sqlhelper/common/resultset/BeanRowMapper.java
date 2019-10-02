package com.jn.sqlhelper.common.resultset;

import com.jn.langx.util.Preconditions;
import com.jn.sqlhelper.common.ddlmodel.ResultSetDescription;
import com.jn.sqlhelper.common.utils.FieldInfo;
import com.jn.sqlhelper.common.utils.FieldSetterAndGetterClassParser;

import java.sql.ResultSet;
import java.util.Map;

public class BeanRowMapper<T> implements RowMapper<T> {

    private Class<T> targetClass;

    public BeanRowMapper(Class<T> beanClass) {
        Preconditions.checkNotNull(beanClass);
        this.targetClass = beanClass;
        FieldSetterAndGetterClassParser classParser = new FieldSetterAndGetterClassParser();
        classParser.setHierachial(true);
        classParser.setZeroParameterConstructor(true);
        this.fieldMap = classParser.parse(targetClass);
    }

    private Map<String, FieldInfo> fieldMap;

    @Override
    public T mapping(ResultSet row, int currentRowIndex, ResultSetDescription resultSetDescription) {

        return null;
    }
}
