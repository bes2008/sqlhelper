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
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.function.Predicate2;
import com.jn.langx.util.pattern.patternset.AntPathMatcher;
import com.jn.langx.util.struct.Holder;
import com.jn.sqlhelper.datasource.key.DataSourceKey;
import com.jn.sqlhelper.datasource.key.parser.DataSourceKeyDataSourceParser;
import com.jn.sqlhelper.datasource.key.parser.RandomDataSourceKeyParser;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class DataSourceRegistry implements Registry<DataSourceKey, DataSource> {
    private volatile DataSourceKey primary = null;
    private ConcurrentHashMap<DataSourceKey, NamedDataSource> dataSourceRegistry = new ConcurrentHashMap<DataSourceKey, NamedDataSource>();
    private DataSourceKeyDataSourceParser keyParser = RandomDataSourceKeyParser.INSTANCE;
    private Set<DataSourceKey> nonExistDSKeys = new CopyOnWriteArraySet<DataSourceKey>();

    public void register(DataSourceKey key, DataSource dataSource) {
        Preconditions.checkNotEmpty(key, "the jdbc datasource key is null or empty");
        Preconditions.checkArgument(key.isAvailable(), "the jdbc datasource key is invalid: {}", key);
        Preconditions.checkNotNull(dataSource);

        dataSourceRegistry.put(key, DataSources.toNamedDataSource(dataSource, key));
        if (primary == null && DataSources.DATASOURCE_GROUP_DEFAULT.equals(key.getGroup())) {
            primary = key;
        }
    }

    @Override
    public void register(DataSource dataSource) {
        NamedDataSource namedDataSource = wrap(dataSource);
        register(namedDataSource.getDataSourceKey(), namedDataSource);
    }


    @Override
    public NamedDataSource get(DataSourceKey key) {
        return dataSourceRegistry.get(key);
    }

    public List<DataSourceKey> findKeys(DataSourceKey keypattern) {
        Preconditions.checkNotNull(keypattern);
        Preconditions.checkArgument(keypattern.isAvailable(), "the key is invalid: {}", keypattern);

        NamedDataSource namedDataSource = get(keypattern);
        if (namedDataSource != null) {
            return Collects.newArrayList(keypattern);
        }

        // 已确定的不存在的
        if (nonExistDSKeys.contains(keypattern)) {
            return Collections.emptyList();
        }

        String name = keypattern.getName();
        if (!name.contains(DataSources.DATASOURCE_NAME_WILDCARD)) {
            nonExistDSKeys.add(keypattern);
            return Collections.emptyList();
        }
        final AntPathMatcher antPathMatcher = new AntPathMatcher(name);
        antPathMatcher.setGlobal(true);

        final String group = keypattern.getGroup();
        List<DataSourceKey> matched = Pipeline.of(dataSourceRegistry.keySet()).filter(new Predicate<DataSourceKey>() {
            @Override
            public boolean test(DataSourceKey dataSourceKey) {
                if (!dataSourceKey.getGroup().equals(group)) {
                    return false;
                }
                return antPathMatcher.match(dataSourceKey.getName());
            }
        }).asList();

        if (Emptys.isNotEmpty(matched)) {
            nonExistDSKeys.add(keypattern);
            return Collections.emptyList();
        }
        return matched;
    }

    /**
     * 只做从已经
     *
     * @param dataSource
     * @return
     */
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
        return DataSources.toNamedDataSource(dataSource, key);
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
}
