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

package com.jn.sqlhelper.datasource.factory.c3p0;

import com.jn.langx.annotation.Name;
import com.jn.langx.annotation.OnClasses;
import com.jn.langx.text.StringTemplates;
import com.jn.sqlhelper.datasource.DataSources;
import com.jn.sqlhelper.datasource.NamedDataSource;
import com.jn.sqlhelper.datasource.config.DataSourceProperties;
import com.jn.sqlhelper.datasource.factory.DataSourceFactory;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * 提供基于 C3p0 连接池的 DataSource Factory
 *
 * @since 3.4.0
 */
@Name(DataSources.DATASOURCE_IMPLEMENT_KEY_C3P0)
@OnClasses({
        "com.mchange.v2.c3p0.WrapperConnectionPoolDataSource",
})
public class C3p0DataSourceFactory implements DataSourceFactory {
    @Override
    public NamedDataSource get(DataSourceProperties dataSourceProperties) {
        if (DataSources.isImplementationKeyMatched(DataSources.DATASOURCE_IMPLEMENT_KEY_C3P0, dataSourceProperties)) {
            DataSource dataSource = C3p0DataSources.createDataSource(dataSourceProperties);
            String name = dataSourceProperties.getName();
            return DataSources.toNamedDataSource(dataSource, name, dataSourceProperties);
        }
        throw new IllegalArgumentException(StringTemplates.formatWithPlaceholder("Illegal datasource implementationKey {}, expected key is {}", dataSourceProperties.getImplementation(), DataSources.DATASOURCE_IMPLEMENT_KEY_C3P0));
    }

    @Override
    public NamedDataSource get(Properties properties) {
        DataSource dataSource = C3p0DataSources.createDataSource(properties);
        String name = properties.getProperty(DataSources.DATASOURCE_PROP_NAME);
        DataSourceProperties dataSourceProperties = C3p0DataSources.toDataSourceProperties(properties);
        return DataSources.toNamedDataSource(dataSource, name, dataSourceProperties);
    }
}
