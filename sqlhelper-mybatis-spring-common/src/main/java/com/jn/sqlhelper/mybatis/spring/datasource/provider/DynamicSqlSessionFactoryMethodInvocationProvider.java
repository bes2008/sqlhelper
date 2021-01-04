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

package com.jn.sqlhelper.mybatis.spring.datasource.provider;

import com.jn.langx.invocation.MethodInvocation;
import com.jn.sqlhelper.datasource.key.DataSourceKey;
import com.jn.sqlhelper.datasource.key.DataSourceKeySelector;
import com.jn.sqlhelper.mybatis.spring.datasource.DynamicSqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Map;

public class DynamicSqlSessionFactoryMethodInvocationProvider implements SqlSessionFactoryProvider<MethodInvocation> {
    private DataSourceKeySelector keySelector;
    private DynamicSqlSessionFactory dynamicSqlSessionFactory;

    public DynamicSqlSessionFactoryMethodInvocationProvider(DynamicSqlSessionFactory dynamicSqlSessionFactory, DataSourceKeySelector keySelector) {
        this.keySelector = keySelector;
        this.dynamicSqlSessionFactory = dynamicSqlSessionFactory;
    }

    @Override
    public SqlSessionFactory get(MethodInvocation invocation) {
        boolean needClear = false;
        if (DataSourceKeySelector.getCurrent() == null) {
            keySelector.select(null);
            needClear = true;
        }

        if (DataSourceKeySelector.getCurrent() != null) {
            Map<DataSourceKey, SqlSessionFactory> factoryMap = dynamicSqlSessionFactory.getDelegates();
            SqlSessionFactory delegate = factoryMap.get(DataSourceKeySelector.getCurrent());
            if (needClear) {
                DataSourceKeySelector.removeCurrent();
            }
            return delegate;
        }
        return null;
    }
}
