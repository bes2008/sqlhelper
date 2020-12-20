package com.jn.sqlhelper.datasource.key.parser;

import com.jn.sqlhelper.datasource.annotation.DataSource;
import com.jn.sqlhelper.datasource.key.DataSourceKey;

public class DataSourceAnnotationParser extends AbstractDataSourceKeyAnnotationParser<DataSource> {
    @Override
    public Class<DataSource> getAnnotation() {
        return DataSource.class;
    }

    @Override
    protected DataSourceKey internalParse(DataSource dataSource) {
        return new DataSourceKey(dataSource.group(), dataSource.value());
    }
}
