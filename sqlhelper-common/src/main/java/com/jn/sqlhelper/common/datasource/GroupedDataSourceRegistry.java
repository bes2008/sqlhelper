package com.jn.agileway.jdbc.datasource;

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
