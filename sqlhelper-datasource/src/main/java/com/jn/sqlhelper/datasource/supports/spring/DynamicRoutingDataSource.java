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

package com.jn.sqlhelper.datasource.supports.spring;

import com.jn.langx.util.Preconditions;
import com.jn.sqlhelper.datasource.DataSourceRegistry;
import com.jn.sqlhelper.datasource.DataSourceRegistryAware;
import com.jn.sqlhelper.datasource.DataSources;
import com.jn.sqlhelper.datasource.key.DataSourceKey;
import com.jn.sqlhelper.datasource.key.MethodInvocationDataSourceKeySelector;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.jdbc.datasource.lookup.DataSourceLookup;
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;

import javax.sql.DataSource;

public class DynamicRoutingDataSource extends AbstractRoutingDataSource implements DataSourceRegistryAware {
    private DataSourceRegistry registry;

    private class DynamicDataSourceKeyLookup implements DataSourceLookup {
        @Override
        public DataSource getDataSource(String dataSourceName) throws DataSourceLookupFailureException {
            DataSourceKey key = DataSources.buildDataSourceKey(dataSourceName);
            return registry.get(key);
        }
    }


    @Override
    public void setDataSourceRegistry(DataSourceRegistry registry) {
        this.registry = registry;
        setDataSourceLookup(null);
    }

    @Override
    public void setDataSourceLookup(DataSourceLookup dataSourceLookup) {
        if (dataSourceLookup == null) {
            dataSourceLookup = new DynamicDataSourceKeyLookup();
        }
        super.setDataSourceLookup(dataSourceLookup);
    }

    @Override
    protected Object resolveSpecifiedLookupKey(Object lookupKey) {
        Preconditions.checkArgument(lookupKey instanceof String);
        return DataSources.buildDataSourceKey((String) lookupKey);
    }

    @Override
    protected DataSource resolveSpecifiedDataSource(Object dataSource) throws IllegalArgumentException {
        if (dataSource instanceof DataSourceKey) {
            return registry.get((DataSourceKey) dataSource);
        }
        return super.resolveSpecifiedDataSource(dataSource);
    }

    @Override
    protected DataSource determineTargetDataSource() {
        DataSourceKey dataSourceKey = MethodInvocationDataSourceKeySelector.getCurrent();

        DataSource dataSource = null;
        if (dataSourceKey != null) {
            dataSource = registry.get(dataSourceKey);
        }
        if (dataSource == null) {
            dataSource = super.determineTargetDataSource();
        }
        return dataSource;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return MethodInvocationDataSourceKeySelector.getCurrent();
    }
}
