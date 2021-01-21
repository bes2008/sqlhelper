/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-2.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jn.sqlhelper.datasource.supports.spring.transaction.definition;

import com.jn.langx.util.Objs;
import com.jn.sqlhelper.common.transaction.TransactionDefinition;
import com.jn.sqlhelper.common.transaction.definition.parser.NamedTransactionDefinitionParser;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;

import java.lang.reflect.Method;

/**
 * 用于兼容Spring Transaction 定义
 * @since 3.4.1
 */
public class SpringTransactionAttributeSourceAdapter implements NamedTransactionDefinitionParser<Method> {
    private String name;
    private TransactionAttributeSource source;

    public SpringTransactionAttributeSourceAdapter(TransactionAttributeSource source) {
        this.source = source;
    }

    @Override
    public void setName(String s) {
        this.name = s;
    }

    @Override
    public String getName() {
        return Objs.useValueIfEmpty(this.name, source.toString());
    }

    @Override
    public TransactionDefinition parse(Method method) {
        TransactionAttribute attribute = source.getTransactionAttribute(method, method.getDeclaringClass());
        if (attribute != null) {
            return new SpringTransactionAttributeDefinition(attribute);
        }
        return null;
    }
}
