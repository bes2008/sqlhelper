/*
 * Copyright 2020 the original author or authors.
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

package com.jn.sqlhelper.datasource.spring.aop;

import com.jn.agileway.aop.adapter.aopalliance.MethodInvocationAdapter;
import com.jn.sqlhelper.datasource.key.DataSourceKey;
import com.jn.sqlhelper.datasource.key.DataSourceKeySelector;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class DataSourceKeyChoicesAnnotationMethodInterceptor implements MethodInterceptor {

    private DataSourceKeySelector keySelector;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        com.jn.langx.invocation.MethodInvocation ivk = new MethodInvocationAdapter(invocation);
        DataSourceKey key = keySelector.select(null, ivk);
        if (key != null) {
            DataSourceKeySelector.addChoice(key);
            try {
                return invocation.proceed();
            } finally {
                DataSourceKeySelector.removeChoice(key);
            }
        } else {
            return invocation.proceed();
        }
    }

    public void setKeySelector(DataSourceKeySelector keySelector) {
        this.keySelector = keySelector;
    }
}
