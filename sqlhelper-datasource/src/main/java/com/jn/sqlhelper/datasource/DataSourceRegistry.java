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
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.function.Predicate2;
import com.jn.langx.util.struct.Holder;
import com.jn.sqlhelper.datasource.key.DataSourceKey;
import com.jn.sqlhelper.datasource.key.DataSourceKeyParser;
import com.jn.sqlhelper.datasource.key.RandomDataSourceKeyParser;

import javax.sql.DataSource;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class DataSourceRegistry implements Registry<DataSourceKey, DataSource> {
    private volatile DataSourceKey defaultKey = null;
    private ConcurrentHashMap<DataSourceKey, NamedDataSource> dataSourceRegistry = new ConcurrentHashMap<DataSourceKey, NamedDataSource>();
    private DataSourceKeyParser keyParser = RandomDataSourceKeyParser.INSTANCE;

    public void register(DataSourceKey key, DataSource dataSource) {
        Preconditions.checkNotEmpty(key, "the datasource key is null or empty");
        Preconditions.checkNotNull(dataSource);
        dataSourceRegistry.put(key, DataSources.toNamedDataSource(dataSource, key));
        if (defaultKey == null) {
            defaultKey = key;
        }
    }

    @Override
    public NamedDataSource get(DataSourceKey key) {
        return dataSourceRegistry.get(key);
    }

    @Override
    public void register(DataSource dataSource) {
        NamedDataSource namedDataSource = wrap(dataSource);
        register(namedDataSource.getDataSourceKey(), namedDataSource);
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


    public void setKeyParser(DataSourceKeyParser keyParser) {
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

    public DataSourceKey getDefaultKey() {
        return defaultKey;
    }

    public int size() {
        return dataSourceRegistry.size();
    }
}
