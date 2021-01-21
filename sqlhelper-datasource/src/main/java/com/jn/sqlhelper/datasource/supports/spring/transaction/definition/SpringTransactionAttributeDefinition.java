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

import com.jn.langx.util.Strings;
import com.jn.sqlhelper.common.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.TransactionAttribute;

/**
 * 通过兼容Spring里的 TransactionAttribute来达到兼容Spring 事务定义的目的
 * @since 3.4.1
 */
public class SpringTransactionAttributeDefinition implements TransactionDefinition {
    private TransactionAttribute attribute;
    private String name;

    public SpringTransactionAttributeDefinition(TransactionAttribute attribute) {
        this.attribute = attribute;
    }

    @Override
    public int getIsolationLevel() {
        return attribute.getIsolationLevel();
    }

    @Override
    public boolean isReadonly() {
        return attribute.isReadOnly();
    }

    @Override
    public void setName(String s) {
        this.name = s;
    }

    @Override
    public String getName() {
        return Strings.useValueIfEmpty(name, attribute.getName());
    }

    @Override
    public boolean rollbackOn(Throwable ex) {
        return attribute.rollbackOn(ex);
    }
}
