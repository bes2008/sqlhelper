package com.jn.agileway.jdbc.datasource;

import com.jn.agileway.jdbc.datasource.factory.DataSourceProperties;
import com.jn.langx.factory.Factory;

import javax.sql.DataSource;
import java.util.Properties;

public interface DataSourceFactory extends Factory<DataSourceProperties, NamedDataSource> {
    @Override
    NamedDataSource get(DataSourceProperties dataSourceProperties);

    NamedDataSource get(Properties properties);
}
