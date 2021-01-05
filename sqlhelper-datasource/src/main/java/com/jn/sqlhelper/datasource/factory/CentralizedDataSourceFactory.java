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

package com.jn.sqlhelper.datasource.factory;

import com.jn.langx.util.Preconditions;
import com.jn.langx.util.reflect.Reflects;
import com.jn.sqlhelper.datasource.DataSourceRegistry;
import com.jn.sqlhelper.datasource.DataSources;
import com.jn.sqlhelper.datasource.NamedDataSource;
import com.jn.sqlhelper.datasource.config.DataSourceProperties;
import com.jn.sqlhelper.datasource.key.DataSourceKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class CentralizedDataSourceFactory implements DataSourceFactory {
    private static final Logger logger = LoggerFactory.getLogger(CentralizedDataSourceFactory.class);
    private DataSourceRegistry registry;

    public DataSourceRegistry getRegistry() {
        return registry;
    }

    public void setRegistry(DataSourceRegistry registry) {
        this.registry = registry;
    }

    @Override
    public NamedDataSource get(DataSourceProperties dataSourceProperties) {
        Preconditions.checkNotNull(registry);
        String name = dataSourceProperties.getName();
        Preconditions.checkNotNull(name, "the datasource name is null");

        DataSourceKey key = dataSourceProperties.getDataSourceKey();
        NamedDataSource dataSource = registry.get(dataSourceProperties.getDataSourceKey());
        if (dataSource == null) {
            String implementationKey = dataSourceProperties.getImplementation();
            DataSourceFactory delegate = DataSourceFactoryProvider.getInstance().findSuitableDataSourceFactory(implementationKey, key);
            logger.info("Create jdbc datasource {} with the factory: {}", key, Reflects.getFQNClassName(delegate.getClass()));
            dataSource = delegate.get(dataSourceProperties);
            if (dataSource != null) {
                registry.register(key, dataSource);
                dataSource = registry.get(key);
            }
        }
        return dataSource;
    }


    @Override
    public NamedDataSource get(Properties properties) {
        Preconditions.checkNotNull(registry);
        String name = properties.getProperty(DataSources.DATASOURCE_PROP_NAME);
        Preconditions.checkNotNull(name, "the datasource name is null");
        String group = properties.getProperty(DataSources.DATASOURCE_PROP_GROUP, DataSources.DATASOURCE_PRIMARY_GROUP);

        DataSourceKey key = new DataSourceKey(group, name);
        NamedDataSource dataSource = registry.get(key);
        if (dataSource == null) {
            String implementationKey = properties.getProperty(DataSources.DATASOURCE_PROP_IMPLEMENTATION);
            DataSourceFactory delegate = DataSourceFactoryProvider.getInstance().findSuitableDataSourceFactory(implementationKey, key);
            dataSource = delegate.get(properties);
            if (dataSource != null) {
                registry.register(key, dataSource);
                dataSource = registry.get(key);
            }
        }
        return dataSource;
    }
}
