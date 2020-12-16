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
import com.jn.langx.util.collection.Maps;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.function.Supplier;
import com.jn.langx.util.struct.Holder;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataSourceRegistry implements Registry<DataSourceKey, DataSource> {

    /**
     * key: group
     * subKey: datasource name
     */
    private ConcurrentHashMap<String, Map<String, DataSource>> dataSourceRegistry = new ConcurrentHashMap<String, Map<String, DataSource>>();

    public void register(DataSourceKey key, DataSource dataSource) {
        Preconditions.checkNotEmpty(key, "the datasource key is null or empty");
        Preconditions.checkNotNull(dataSource);

        Map<String, DataSource> dataSources = Maps.putIfAbsent(dataSourceRegistry, key.getGroup(), new Supplier<String, Map<String, DataSource>>() {
            @Override
            public Map<String, DataSource> get(String input) {
                return new HashMap<String, DataSource>();
            }
        });

        Maps.putIfAbsent(dataSources, key.getName(),  dataSource);

    }


    @Override
    public NamedDataSource get(DataSourceKey key) {
        Map<String, DataSource> dataSourceMap = dataSourceRegistry.get(key.getGroup());
        if(dataSourceMap!=null){
            return (NamedDataSource) dataSourceMap.get(key.getName());
        }
        return null;
    }

    @Override
    public void register(DataSource dataSource) {

    }

    public NamedDataSource get(final String dataSourceName) {
        final Holder<DataSource> dataSourceHolder = new Holder<DataSource>();
        Collects.forEach(dataSourceRegistry.values(), new Consumer<Map<String, DataSource>>() {
            @Override
            public void accept(Map<String, DataSource> groupDSs) {
                DataSource dataSource = groupDSs.get(dataSourceName);
                if (dataSource != null) {
                    dataSourceHolder.set(dataSource);
                }
            }
        }, new Predicate<Map<String, DataSource>>() {
            @Override
            public boolean test(Map<String, DataSource> value) {
                return dataSourceHolder.isNull();
            }
        });
        return (NamedDataSource) dataSourceHolder.get();
    }
}
