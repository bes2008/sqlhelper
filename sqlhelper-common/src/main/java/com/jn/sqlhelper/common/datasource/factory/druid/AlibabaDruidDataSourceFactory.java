package com.jn.agileway.jdbc.datasource.factory.druid;

import com.jn.agileway.jdbc.Jdbcs;
import com.jn.agileway.jdbc.datasource.DataSourceFactory;
import com.jn.agileway.jdbc.datasource.DataSources;
import com.jn.agileway.jdbc.datasource.DelegatingNamedDataSource;
import com.jn.agileway.jdbc.datasource.NamedDataSource;
import com.jn.agileway.jdbc.datasource.factory.DataSourceProperties;
import com.jn.langx.annotation.Name;
import com.jn.langx.annotation.OnClasses;
import com.jn.langx.text.StringTemplates;

import javax.sql.DataSource;
import java.util.Properties;

import static com.jn.agileway.jdbc.datasource.DataSources.DATASOURCE_IMPLEMENT_KEY_DRUID;

@Name(DATASOURCE_IMPLEMENT_KEY_DRUID)
@OnClasses({
        "com.alibaba.druid.pool.DruidDataSource",
        "com.alibaba.druid.pool.DruidDataSourceFactory",
})
public class AlibabaDruidDataSourceFactory implements DataSourceFactory {
    @Override
    public NamedDataSource get(DataSourceProperties dataSourceProperties) {
        if (Jdbcs.isImplementationKeyMatched(DATASOURCE_IMPLEMENT_KEY_DRUID, dataSourceProperties)) {
            DataSource dataSource = AlibabaDruidDataSources.createDataSource(dataSourceProperties);
            String name = dataSourceProperties.getName();
            return DelegatingNamedDataSource.of(dataSource, name);
        }
        throw new IllegalArgumentException(StringTemplates.formatWithPlaceholder("Illegal datasource implementationKey {}, expected key is {}", dataSourceProperties.getImplementation(), DATASOURCE_IMPLEMENT_KEY_DRUID));
    }

    @Override
    public NamedDataSource get(Properties properties) {
        DataSource dataSource = AlibabaDruidDataSources.createDataSource(properties);
        String name = properties.getProperty(DataSources.DATASOURCE_NAME);
        return DelegatingNamedDataSource.of(dataSource, name);
    }
}
