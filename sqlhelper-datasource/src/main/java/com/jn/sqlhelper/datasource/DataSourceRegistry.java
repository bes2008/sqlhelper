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

import com.jn.langx.Delegatable;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.registry.Registry;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.concurrent.clhm.ConcurrentLinkedHashMap;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.function.Predicate2;
import com.jn.langx.util.logging.Level;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.pattern.patternset.AntPathMatcher;
import com.jn.langx.util.struct.Holder;
import com.jn.sqlhelper.datasource.key.DataSourceKey;
import com.jn.sqlhelper.datasource.key.parser.DataSourceKeyDataSourceParser;
import com.jn.sqlhelper.datasource.key.parser.RandomDataSourceKeyParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 这是一个支持 负载均衡的 DataSource 容器
 */
public class DataSourceRegistry implements Registry<DataSourceKey, DataSource> {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceRegistry.class);
    /**
     * 可能是确切的值，也可能是个key pattern
     */
    private volatile DataSourceKey primary = null;
    /**
     * 这里的Key 最好是确切的key，不建议使用key Pattern
     */
    private ConcurrentMap<DataSourceKey, NamedDataSource> dataSourceRegistry = new ConcurrentLinkedHashMap.Builder<DataSourceKey, NamedDataSource>()
            .concurrencyLevel(Runtime.getRuntime().availableProcessors())
            .initialCapacity(16)
            .maximumWeightedCapacity(1000)
            .build();
    private DataSourceKeyDataSourceParser keyParser = RandomDataSourceKeyParser.INSTANCE;

    /**
     * 用户在使用时，可能用一些不存在的Key
     */
    private Set<DataSourceKey> nonExistDSKeys = new CopyOnWriteArraySet<DataSourceKey>();

    /**
     * 是否开启故障转移功能
     */
    private volatile boolean failover = true;

    /**
     * 故障的key
     */
    private Set<DataSourceKey> failKeys = new CopyOnWriteArraySet<DataSourceKey>();

    public void register(DataSourceKey key, DataSource dataSource) {
        Preconditions.checkNotEmpty(key, "the jdbc datasource key is null or empty");
        Preconditions.checkArgument(key.isAvailable(), "the jdbc datasource key is invalid: {}", key);
        Preconditions.checkNotNull(dataSource);

        dataSourceRegistry.put(key, DataSources.toNamedDataSource(dataSource, key, null));
        if (primary == null && DataSources.DATASOURCE_PRIMARY_GROUP.equals(key.getGroup())) {
            primary = key;
        }
        if (primary != null) {
            if (DataSources.DATASOURCE_PRIMARY.equals(key)) {
                primary = key;
            }
            if (!DataSources.DATASOURCE_PRIMARY.equals(primary)) {
                primary = new DataSourceKey(DataSources.DATASOURCE_PRIMARY_GROUP, "*");
            }
        }

    }

    @Override
    public void register(DataSource dataSource) {
        NamedDataSource namedDataSource = wrap(dataSource);
        register(namedDataSource.getDataSourceKey(), namedDataSource);
    }

    public NamedDataSource get(String keyString) {
        DataSourceKey key = DataSources.buildDataSourceKey(keyString);
        return get(key);
    }


    @Override
    public NamedDataSource get(DataSourceKey key) {
        if (key == null) {
            return null;
        }
        return dataSourceRegistry.get(key);
    }


    public List<DataSourceKey> findKeys(DataSourceKey groupKeyPattern) {
        Preconditions.checkNotNull(groupKeyPattern);
        Preconditions.checkArgument(groupKeyPattern.isAvailable(), "the key is invalid: {}", groupKeyPattern);

        NamedDataSource namedDataSource = get(groupKeyPattern);
        if (namedDataSource != null) {
            return Collects.newArrayList(groupKeyPattern);
        }

        // 已确定的不存在的
        if (nonExistDSKeys.contains(groupKeyPattern)) {
            return Collections.emptyList();
        }

        String name = groupKeyPattern.getName();
        if (!name.contains(DataSources.DATASOURCE_NAME_WILDCARD)) {
            addNonExistsDataSourceKey(groupKeyPattern);
            return Collections.emptyList();
        }

        // 针对 key pattern 进行匹配
        final AntPathMatcher antPathMatcher = new AntPathMatcher(null);
        antPathMatcher.setGlobal(true);
        antPathMatcher.setPatternExpression(name);

        final String group = groupKeyPattern.getGroup();
        List<DataSourceKey> matched = Pipeline.of(dataSourceRegistry.keySet()).filter(new Predicate<DataSourceKey>() {
            @Override
            public boolean test(DataSourceKey dataSourceKey) {
                if (!dataSourceKey.getGroup().equals(group)) {
                    return false;
                }
                return antPathMatcher.match(dataSourceKey.getName());
            }
        }).asList();

        // 如果没有匹配到任何数据源，则加入不存在的 key pattern 序列
        if (Emptys.isEmpty(matched)) {
            addNonExistsDataSourceKey(groupKeyPattern);
            return Collections.emptyList();
        }

        if (failover) {
            matched = Pipeline.of(matched).filter(new Predicate<DataSourceKey>() {
                @Override
                public boolean test(DataSourceKey dataSourceKey) {
                    return !failKeys.contains(dataSourceKey);
                }
            }).asList();
        }

        return matched;
    }

    private void addNonExistsDataSourceKey(DataSourceKey keyPattern) {
        Loggers.log(3, logger, Level.WARN, null, "Using a key that is not exist: {}", keyPattern);
        nonExistDSKeys.add(keyPattern);
    }

    private DataSourceKey intervalParse(@NonNull DataSource dataSource) {
        final List<DataSource> toComparedDataSourceList = Collects.newArrayList();
        DataSource tmpDs = dataSource;
        while (tmpDs != null) {
            if (tmpDs instanceof NamedDataSource) {
                return ((NamedDataSource) tmpDs).getDataSourceKey();
            }
            toComparedDataSourceList.add(tmpDs);
            if (tmpDs instanceof Delegatable) {
                Object delegate = ((Delegatable) tmpDs).getDelegate();
                if (delegate instanceof DataSource) {
                    tmpDs = (DataSource) delegate;
                } else {
                    break;
                }
            } else {
                break;
            }
        }

        final Holder<DataSourceKey> dataSourceKeyHolder = new Holder<DataSourceKey>();
        Collects.forEach(dataSourceRegistry, new Consumer2<DataSourceKey, NamedDataSource>() {
            @Override
            public void accept(DataSourceKey key, final NamedDataSource ds) {
                if (Collects.anyMatch(toComparedDataSourceList, new Predicate<DataSource>() {
                    @Override
                    public boolean test(DataSource toCompared) {
                        if (ds == toCompared) {
                            return true;
                        }
                        if (ds instanceof Delegatable) {
                            Object delegate = ((Delegatable) ds).getDelegate();
                            if (delegate instanceof DataSource) {
                                DataSource delegateDs = (DataSource) delegate;
                                if (delegateDs == toCompared) {
                                    return true;
                                }
                            }
                        }
                        return false;
                    }
                })) {
                    dataSourceKeyHolder.set(key);
                }
            }
        }, new Predicate2<DataSourceKey, NamedDataSource>() {
            @Override
            public boolean test(DataSourceKey key, NamedDataSource value) {
                return !dataSourceKeyHolder.isNull();
            }
        });
        return dataSourceKeyHolder.get();
    }


    public void setKeyParser(DataSourceKeyDataSourceParser keyParser) {
        this.keyParser = keyParser;
    }

    public NamedDataSource wrap(DataSource dataSource) {
        DataSourceKey key = null;
        if (dataSource instanceof NamedDataSource) {
            key = ((NamedDataSource) dataSource).getDataSourceKey();
        }
        if (key == null) {
            key = intervalParse(dataSource);
        }
        if (key == null && keyParser != null) {
            key = keyParser.parse(dataSource);
        }
        if (key == null) {
            key = RandomDataSourceKeyParser.INSTANCE.parse(dataSource);
        }
        return DataSources.toNamedDataSource(dataSource, key, null);
    }

    public DataSourceKey getPrimary() {
        if (primary == null) {
            if (dataSourceRegistry.isEmpty()) {
                throw new IllegalStateException("Can't find any valid jdbc datasource");
            }
            if (dataSourceRegistry.size() == 1) {
                return Collects.findFirst(dataSourceRegistry.keySet());
            } else {
                throw new IllegalStateException(StringTemplates.formatWithPlaceholder("Can't find the primary jdbc datasource, all the registered dataSources: {}", Strings.join(", ", dataSourceRegistry.keySet())));
            }
        } else {
            return primary;
        }

    }

    public int size() {
        return dataSourceRegistry.size();
    }

    public boolean isFailover() {
        return failover;
    }

    public void setFailover(boolean failover) {
        this.failover = failover;
    }


    public List<DataSourceKey> allKeys() {
        return Collects.asList(dataSourceRegistry.keySet());
    }

}
