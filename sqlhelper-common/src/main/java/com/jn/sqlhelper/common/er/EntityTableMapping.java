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
import com.jn.langx.util.collection.Collects;

import java.lang.ref.SoftReference;
import java.util.Map;

/**
 * 代表了实体类和表的映射关系
 */
public class EntityTableMapping {
    /**
     * 实体类
     */
    private SoftReference<Class<?>> entityClass;

    /**
     * 实体类对应的数据库表
     */
    private String table;

    /**
     * 字段与列名映射
     */
    private Map<String, String> columnMappings = Collects.emptyHashMap(true);

    /**
     * Entity类中的哪个字段代表了Tenant Column
     */
    private String tenantField;

    public Class getEntityClass() {
        return entityClass.get();
    }

    public void setEntityClass(Class entityClass) {
        this.entityClass = new SoftReference<Class<?>>(entityClass);
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Map<String, String> getColumnMappings() {
        return columnMappings;
    }

    public void setColumnMappings(Map<String, String> columnMappings) {
        this.columnMappings = columnMappings;
    }

    public String getTenantField() {
        return tenantField;
    }

    public void setTenantField(String tenantField) {
        this.tenantField = tenantField;
    }

    public boolean hasTenantColumn() {
        return Emptys.isNotEmpty(tenantField) && columnMappings.containsKey(tenantField);
    }

    public String getTenantColumn() {
        if (Emptys.isNotEmpty(tenantField)) {
            return columnMappings.get(tenantField);
        }
        return null;
    }
}
