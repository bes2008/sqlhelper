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

import com.jn.sqlhelper.datasource.key.DataSourceKey;
import com.jn.sqlhelper.datasource.key.DataSourceKeySelector;
import com.jn.sqlhelper.mybatis.session.factory.SqlSessionFactoryProvider;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DynamicDataSourceSqlSessionFactoryProvider<I> implements SqlSessionFactoryProvider<I> {
    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceSqlSessionFactoryProvider.class);
    protected DataSourceKeySelector<I> selector;
    protected DynamicSqlSessionFactory dynamicSqlSessionFactory;

    public void setSelector(DataSourceKeySelector<I> selector) {
        this.selector = selector;
    }

    public void setDynamicSqlSessionFactory(DynamicSqlSessionFactory dynamicSqlSessionFactory) {
        this.dynamicSqlSessionFactory = dynamicSqlSessionFactory;
    }

    @Override
    public SqlSessionFactory get(I i) {
        return doGet(i);
    }

    protected SqlSessionFactory doGet(I i) {
        Map<DataSourceKey, DelegatingSqlSessionFactory> factoryMap = dynamicSqlSessionFactory.getDelegates();
        DataSourceKey key = selector.select(i);
        if (key == null || !key.isAvailable()) {
            logger.warn("the datasource key is null or not available");
            return null;
        }
        return factoryMap.get(key);
    }
}
