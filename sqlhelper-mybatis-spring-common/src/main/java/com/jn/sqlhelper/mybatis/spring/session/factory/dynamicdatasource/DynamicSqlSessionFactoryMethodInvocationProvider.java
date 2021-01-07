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

package com.jn.sqlhelper.mybatis.spring.session.factory.dynamicdatasource;

import com.jn.langx.invocation.MethodInvocation;
import com.jn.sqlhelper.datasource.key.DataSourceKey;
import com.jn.sqlhelper.datasource.key.MethodInvocationDataSourceKeySelector;
import com.jn.sqlhelper.mybatis.session.factory.SqlSessionFactoryProvider;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Map;

public class DynamicSqlSessionFactoryMethodInvocationProvider implements SqlSessionFactoryProvider<MethodInvocation> {
    private MethodInvocationDataSourceKeySelector keySelector;
    private DynamicSqlSessionFactory dynamicSqlSessionFactory;

    public DynamicSqlSessionFactoryMethodInvocationProvider(DynamicSqlSessionFactory dynamicSqlSessionFactory, MethodInvocationDataSourceKeySelector keySelector) {
        this.keySelector = keySelector;
        this.dynamicSqlSessionFactory = dynamicSqlSessionFactory;
    }

    @Override
    public SqlSessionFactory get(MethodInvocation invocation) {
        boolean needClear = false;
        if (MethodInvocationDataSourceKeySelector.getCurrent() == null) {
            keySelector.select(invocation);
            needClear = true;
        }

        if (MethodInvocationDataSourceKeySelector.getCurrent() != null) {
            Map<DataSourceKey, SqlSessionFactory> factoryMap = dynamicSqlSessionFactory.getDelegates();
            SqlSessionFactory delegate = factoryMap.get(MethodInvocationDataSourceKeySelector.getCurrent());
            if (needClear) {
                MethodInvocationDataSourceKeySelector.removeCurrent();
            }
            return delegate;
        }
        return null;
    }
}
