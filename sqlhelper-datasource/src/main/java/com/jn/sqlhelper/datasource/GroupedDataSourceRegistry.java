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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GroupedDataSourceRegistry implements Registry<String, NamedDataSource> {

    /**
     * key: group
     * subKey: datasource name
     */
    private ConcurrentHashMap<String, Map<String, NamedDataSource>> dataSourceRegistry = new ConcurrentHashMap<String, Map<String, NamedDataSource>>();

    public void register(String group, NamedDataSource dataSource) {
        Preconditions.checkNotEmpty(group, "the datasource group is null or empty");
        Preconditions.checkNotNull(dataSource);

        Map<String, NamedDataSource> dataSources = Maps.putIfAbsent(dataSourceRegistry, group, new Supplier<String, Map<String, NamedDataSource>>() {
            @Override
            public Map<String, NamedDataSource> get(String input) {
                return new HashMap<String, NamedDataSource>();
            }
        });

        Maps.putIfAbsent(dataSources, dataSource.getName(), dataSource);

    }

    @Override
    public void register(NamedDataSource dataSource) {
        register(DataSources.DATASOURCE_GROUP_DEFAULT, dataSource);
    }

    public NamedDataSource get(final String dataSourceName) {
        final Holder<NamedDataSource> dataSourceHolder = new Holder<NamedDataSource>();
        Collects.forEach(dataSourceRegistry.values(), new Consumer<Map<String, NamedDataSource>>() {
            @Override
            public void accept(Map<String, NamedDataSource> groupDSs) {
                NamedDataSource dataSource = groupDSs.get(dataSourceName);
                if (dataSource != null) {
                    dataSourceHolder.set(dataSource);
                }
            }
        }, new Predicate<Map<String, NamedDataSource>>() {
            @Override
            public boolean test(Map<String, NamedDataSource> value) {
                return dataSourceHolder.isNull();
            }
        });
        return dataSourceHolder.get();
    }
}
