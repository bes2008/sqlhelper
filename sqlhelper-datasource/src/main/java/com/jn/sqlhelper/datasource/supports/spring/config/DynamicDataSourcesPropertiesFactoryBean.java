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

package com.jn.sqlhelper.datasource.supports.spring.config;

import com.jn.sqlhelper.datasource.config.DynamicDataSourcesProperties;
import org.springframework.beans.factory.FactoryBean;

/**
 * 提供基于FactoryBean 的方式来自定义 DynamicDataSourcesProperties
 */
public interface DynamicDataSourcesPropertiesFactoryBean extends FactoryBean<DynamicDataSourcesProperties> {
    @Override
    DynamicDataSourcesProperties getObject() throws Exception;

    @Override
    Class<DynamicDataSourcesProperties> getObjectType();

    @Override
    boolean isSingleton();
}
