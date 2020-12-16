package com.jn.agileway.jdbc.datasource.factory;

import com.jn.langx.util.Preconditions;

import java.util.Properties;

public class CentralizedDataSourceFactory implements DataSourceFactory {
    private GroupedDataSourceRegistry registry;

    public GroupedDataSourceRegistry getRegistry() {
        return registry;
    }

    public void setRegistry(GroupedDataSourceRegistry registry) {
        this.registry = registry;
    }

    @Override
    public NamedDataSource get(DataSourceProperties dataSourceProperties) {
        Preconditions.checkNotNull(registry);
        String name = dataSourceProperties.getName();
        Preconditions.checkNotNull(name, "the datasource name is null");

        NamedDataSource dataSource = registry.get(name);
        if (dataSource == null) {
            String implementationKey = dataSourceProperties.getImplementation();
            DataSourceFactory delegate = DataSourceFactoryProvider.getInstance().get(implementationKey);
            if (delegate != null) {
                dataSource = delegate.get(dataSourceProperties);
            }
            if (dataSource != null) {
                registry.register(dataSourceProperties.getGroup(), dataSource);
            }
        }
        return dataSource;
    }

    @Override
    public NamedDataSource get(Properties properties) {
        Preconditions.checkNotNull(registry);
        String name = properties.getProperty(DataSources.DATASOURCE_NAME);
        Preconditions.checkNotNull(name, "the datasource name is null");

        NamedDataSource dataSource = registry.get(name);
        if (dataSource == null) {
            String implementationKey = properties.getProperty(DataSources.DATASOURCE_IMPLEMENT);
            DataSourceFactory delegate = DataSourceFactoryProvider.getInstance().get(implementationKey);
            if (delegate != null) {
                dataSource = delegate.get(properties);
            }
            if (dataSource != null) {
               String group =properties.getProperty(DataSources.DATASOURCE_GROUP, DataSources.DATASOURCE_GROUP_DEFAULT);
                registry.register(group, dataSource);
            }
        }
        return dataSource;
    }
}
