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

package com.jn.sqlhelper.datasource.spring.boot;


import com.jn.langx.util.Emptys;
import com.jn.sqlhelper.datasource.DataSources;
import com.jn.sqlhelper.datasource.key.DataSourceKey;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import java.util.UUID;

/**
 * 兼容Spring DataSourceProperties 配置，将其适配为 sqlhelper-datasource 里的DataSourceProperties
 *
 * @since 3.4.1
 */
public class SpringDataSourcePropertiesAdapter {
    public static com.jn.sqlhelper.datasource.config.DataSourceProperties adapt(DataSourceProperties properties) {
        com.jn.sqlhelper.datasource.config.DataSourceProperties dataSourceProperties = new com.jn.sqlhelper.datasource.config.DataSourceProperties();
        dataSourceProperties.setUsername(properties.getUsername());
        dataSourceProperties.setPassword(properties.getPassword());
        dataSourceProperties.setDriverClassName(properties.getDriverClassName());
        String name = properties.getName();
        if (Emptys.isEmpty(name)) {
            name = UUID.randomUUID().toString();
        } else {
            DataSourceKey key = null;
            try {
                key = DataSources.buildDataSourceKey(name);

            } catch (Throwable ex) {
                // ignore it
            }
            if (key != null) {
                dataSourceProperties.setGroup(key.getGroup());
                dataSourceProperties.setName(key.getName());
            } else {
                dataSourceProperties.setName(name);
            }
        }
        dataSourceProperties.setName(name);
        dataSourceProperties.setUrl(properties.getUrl());
        return dataSourceProperties;
    }
}
