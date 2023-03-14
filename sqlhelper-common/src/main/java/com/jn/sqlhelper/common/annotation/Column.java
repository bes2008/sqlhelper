
/*
 * Copyright 2019 the original author or authors.
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

package com.jn.sqlhelper.common.annotation;

import com.jn.langx.util.converter.NoopConverter;
import com.jn.sqlhelper.common.ddl.model.internal.JdbcType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.jn.sqlhelper.common.ddl.model.internal.JdbcType.UNKNOWN;

/**
 * 代表数据库的列
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
public @interface Column {
    /**
     * 这里是个数组，通常情况下，一个entity的某个字段只能代表一个列
     * 如果这里配置了多个，则说明不同的数据库上可能有不同的名称。
     */
    String[] value() default {};

    JdbcType jdbcType() default UNKNOWN;

    Class converter() default NoopConverter.class;
}
