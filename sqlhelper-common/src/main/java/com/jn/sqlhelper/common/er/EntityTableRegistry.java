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

import com.jn.langx.annotation.Singleton;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.ConcurrentReferenceHashMap;
import com.jn.langx.util.reflect.reference.ReferenceType;

@Singleton
public class EntityTableRegistry {
    /**
     * Key: entity class
     * Value：Entity Table Mapping
     */
    private ConcurrentReferenceHashMap<Class<?>, EntityTableMapping> map = new ConcurrentReferenceHashMap<Class<?>, EntityTableMapping>(1000, 0.95f, Runtime.getRuntime().availableProcessors(), ReferenceType.SOFT, ReferenceType.STRONG);

    public EntityTableMapping getEntityTableMapping(Class entityClass) {
        Preconditions.checkNotNull(entityClass);
        EntityTableMapping mapping = map.get(entityClass);
        if (mapping == null) {
            mapping = new DefaultEntityTableMappingParser().parse(entityClass);
            map.putIfAbsent(entityClass, mapping);

        }
        EntityTableMapping mapping0 = map.get(entityClass);
        if (mapping0 != null) {
            mapping = mapping0;
        }
        return mapping;
    }
}
