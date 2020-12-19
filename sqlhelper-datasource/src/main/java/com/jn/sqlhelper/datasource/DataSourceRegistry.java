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

package com.jn.sqlhelper.datasource;

import com.jn.langx.registry.Registry;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.function.Predicate2;
import com.jn.langx.util.struct.Holder;
import com.jn.sqlhelper.datasource.key.DataSourceKey;
import com.jn.sqlhelper.datasource.key.DataSourceKeyParser;
import com.jn.sqlhelper.datasource.key.RandomDataSourceKeyParser;

import javax.sql.DataSource;
import java.util.concurrent.ConcurrentHashMap;

public class DataSourceRegistry implements Registry<DataSourceKey, DataSource>, DataSourceKeyParser<DataSource> {

    private ConcurrentHashMap<DataSourceKey, NamedDataSource> dataSourceRegistry = new ConcurrentHashMap<DataSourceKey, NamedDataSource>();
    private DataSourceKeyParser keyParser = RandomDataSourceKeyParser.INSTANCE;

    public void register(DataSourceKey key, DataSource dataSource) {
        Preconditions.checkNotEmpty(key, "the datasource key is null or empty");
        Preconditions.checkNotNull(dataSource);
        dataSourceRegistry.put(key, DataSources.toNamedDataSource(dataSource, key));
    }

    @Override
    public NamedDataSource get(DataSourceKey key) {
        return dataSourceRegistry.get(key);
    }

    @Override
    public void register(DataSource dataSource) {
        DataSourceKey key = parse(dataSource);
        if (key == null) {
            if (keyParser != null) {
                key = keyParser.parse(dataSource);
            }
        }
        if (key == null) {
            key = RandomDataSourceKeyParser.INSTANCE.parse(dataSource);
        }
        dataSourceRegistry.put(key, DataSources.toNamedDataSource(dataSource, key));
    }

    @Override
    public DataSourceKey parse(DataSource dataSource) {
        if (dataSource instanceof NamedDataSource) {
            NamedDataSource namedDataSource = (NamedDataSource) dataSource;
            return namedDataSource.getDataSourceKey();
        }
        final Holder<DataSourceKey> dataSourceKeyHolder = new Holder<DataSourceKey>();
        Collects.forEach(dataSourceRegistry, new Consumer2<DataSourceKey, NamedDataSource>() {
            @Override
            public void accept(DataSourceKey key, NamedDataSource value) {

            }
        }, new Predicate2<DataSourceKey, NamedDataSource>() {
            @Override
            public boolean test(DataSourceKey key, NamedDataSource value) {
                return !dataSourceKeyHolder.isNull();
            }
        });
        return dataSourceKeyHolder.get();
    }

    public DataSourceKeyParser getKeyParser() {
        return keyParser;
    }

    public void setKeyParser(DataSourceKeyParser keyParser) {
        this.keyParser = keyParser;
    }
}
