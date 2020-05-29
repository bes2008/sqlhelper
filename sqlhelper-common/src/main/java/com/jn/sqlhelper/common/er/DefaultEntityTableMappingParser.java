/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the LGPL, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jn.sqlhelper.common.er;

import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.reflect.classparse.FieldInfo;
import com.jn.langx.util.reflect.classparse.FieldSetterAndGetterClassParser;
import com.jn.sqlhelper.common.annotation.Column;
import com.jn.sqlhelper.common.annotation.Table;
import com.jn.sqlhelper.common.annotation.Tenant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public class DefaultEntityTableMappingParser implements EntityTableParser {
    private FieldSetterAndGetterClassParser setterAndGetterClassParser;
    private static final Logger logger = LoggerFactory.getLogger(DefaultEntityTableMappingParser.class);

    public DefaultEntityTableMappingParser() {
        this.setterAndGetterClassParser = new FieldSetterAndGetterClassParser();
        this.setterAndGetterClassParser.setHierachial(true);
    }

    @Override
    public EntityTableMapping parse(Class<?> entityClass) {
        Preconditions.checkNotNull(entityClass);
        EntityTableMapping mapping = new EntityTableMapping();
        parseTable(entityClass, mapping);
        return mapping;
    }

    private void parseTable(Class<?> entityClass, EntityTableMapping mapping) {
        if (Reflects.hasAnnotation(entityClass, Table.class)) {
            Table table = Reflects.getAnnotation(entityClass, Table.class);
            if (Emptys.isNotEmpty(table.value())) {
                mapping.setTable(table.value()[0]);
                return;
            }
        }
        mapping.setTable(Reflects.getSimpleClassName(entityClass));
    }

    private void parseFields(Class<?> entityClass, final EntityTableMapping mapping) {
        Map<String, FieldInfo> fieldInfoMap = setterAndGetterClassParser.parse(entityClass);
        Collects.forEach(fieldInfoMap, new Consumer2<String, FieldInfo>() {
            @Override
            public void accept(String fieldName, FieldInfo fieldInfo) {
                parseField(fieldInfo, mapping);
            }
        });
    }

    private void parseField(FieldInfo fieldInfo, EntityTableMapping mapping) {
        parseAsColumn(fieldInfo, mapping);
        parseAsTenant(fieldInfo, mapping);
    }

    private void parseAsTenant(FieldInfo fieldInfo, EntityTableMapping mapping) {

        Field field = fieldInfo.getField();
        Tenant tenant = Reflects.getAnnotation(field, Tenant.class);
        boolean isTenantColumn = false;
        if (tenant != null) {
            isTenantColumn = true;
        }

        if (!isTenantColumn) {
            Method getter = fieldInfo.getGetter();
            if (getter != null) {
                tenant = Reflects.getAnnotation(getter, Tenant.class);
                if (tenant != null) {
                    isTenantColumn = true;
                }
            }
        }

        if (!isTenantColumn) {
            Method setter = fieldInfo.getSetter();
            if (setter != null) {
                tenant = Reflects.getAnnotation(setter, Tenant.class);
                if (tenant != null) {
                    isTenantColumn = true;
                }
            }
        }

        if (isTenantColumn) {
            String fieldName = fieldInfo.getFieldName();
            if (Emptys.isEmpty(mapping.getTenantField())) {
                mapping.setTenantField(fieldName);
            } else {
                logger.warn("Too may @Tenant in the class {}", Reflects.getFQNClassName(field.getDeclaringClass()));
            }
        }
    }

    private void parseAsColumn(FieldInfo fieldInfo, EntityTableMapping mapping) {
        String fieldName = fieldInfo.getFieldName();
        Field field = fieldInfo.getField();
        Column column = Reflects.getAnnotation(field, Column.class);
        String columnName = null;
        if (column != null) {
            if (Emptys.isNotEmpty(column.value())) {
                columnName = column.value()[0];
            }
        }

        if (Emptys.isEmpty(columnName)) {
            Method getter = fieldInfo.getGetter();
            if (getter != null) {
                column = Reflects.getAnnotation(getter, Column.class);
                if (column != null) {
                    if (Emptys.isNotEmpty(column.value())) {
                        columnName = column.value()[0];
                    }
                }
            }
        }

        if (Emptys.isEmpty(columnName)) {
            Method setter = fieldInfo.getSetter();
            if (setter != null) {
                column = Reflects.getAnnotation(setter, Column.class);
                if (column != null) {
                    if (Emptys.isNotEmpty(column.value())) {
                        columnName = column.value()[0];
                    }
                }
            }
        }

        if (Emptys.isEmpty(columnName)) {
            columnName = fieldName;
        }
        mapping.getColumnMappings().put(fieldName, columnName);
    }
}
