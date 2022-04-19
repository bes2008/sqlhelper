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
import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.registry.Registry;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.*;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.concurrent.CommonThreadFactory;
import com.jn.langx.util.concurrent.clhm.ConcurrentLinkedHashMap;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.function.Predicate2;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.logging.Level;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.pattern.patternset.AntPathMatcher;
import com.jn.langx.util.struct.Holder;
import com.jn.sqlhelper.datasource.config.DataSourceProperties;
import com.jn.sqlhelper.datasource.key.DataSourceKey;
import com.jn.sqlhelper.datasource.key.parser.DataSourceKeyDataSourceParser;
import com.jn.sqlhelper.datasource.key.parser.RandomDataSourceKeyParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * 这是一个支持 负载均衡的 DataSource 容器
 */
public class DataSourceRegistry implements Registry<DataSourceKey, DataSource>, Initializable {
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
    private final Set<DataSourceKey> nonExistDSKeys = new CopyOnWriteArraySet<DataSourceKey>();

    /**
     * 故障的key
     */
    private final Set<DataSourceKey> failKeys = new CopyOnWriteArraySet<DataSourceKey>();

    private final ScheduledExecutorService healthCheckExecutor = new ScheduledThreadPoolExecutor(16, new CommonThreadFactory("SQLHelper-DataSource-HealthChecker", true));
    private final Map<DataSourceKey, Future> healthCheckTaskTraceMap = new ConcurrentHashMap<DataSourceKey, Future>();
    /**
     * TimeUnit: seconds
     * 健康检查的周期。
     * <p>
     * 如果大于0，则至少30。
     * 如果小于等于0，则代表不开启健康检查
     */
    private int healthCheckTimeout = 30;
    private boolean inited = false;

    @Override
    public void init() throws InitializationException {
        this.inited = true;
    }

    public void register(DataSourceKey key, DataSource dataSource) {
        Preconditions.checkNotEmpty(key, "the jdbc datasource key is null or empty");
        Preconditions.checkArgument(key.isAvailable(), "the jdbc datasource key is invalid: {}", key);
        Preconditions.checkNotNull(dataSource);

        NamedDataSource namedDataSource = DataSources.toNamedDataSource(dataSource, key, null);
        dataSourceRegistry.put(key, namedDataSource);

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

        enableHealthCheck(namedDataSource);
    }

    private void enableHealthCheck(NamedDataSource namedDataSource) {
        DataSourceKey key = namedDataSource.getDataSourceKey();
        if (healthCheckTimeout > 0) {
            // 此时认为数据源有变化
            if (healthCheckTaskTraceMap.containsKey(key)) {
                Future future = healthCheckTaskTraceMap.remove(key);
                future.cancel(true);
            }

            Future future = healthCheckExecutor.scheduleWithFixedDelay(new HealthCheck(namedDataSource), healthCheckTimeout, healthCheckTimeout, TimeUnit.SECONDS);
            healthCheckTaskTraceMap.put(key, future);
        }
    }

    public void setHealthCheckTimeout(int healthCheckTimeout) {
        Preconditions.checkState(!inited);
        if (healthCheckTimeout > 0) {
            this.healthCheckTimeout = Maths.max(30, healthCheckTimeout);
        } else {
            this.healthCheckTimeout = -1;
        }
    }

    public int getHealthCheckTimeout() {
        return healthCheckTimeout;
    }

    /**
     * 是否开启故障转移功能
     */
    public boolean isFailoverEnabled() {
        return this.healthCheckTimeout > 0;
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
            return Collects.emptyArrayList();
        }

        String name = groupKeyPattern.getName();
        if (!name.contains(DataSources.DATASOURCE_NAME_WILDCARD)) {
            addNonExistsDataSourceKey(groupKeyPattern);
            return Collects.emptyArrayList();
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
            return Collects.emptyArrayList();
        }

        if (isFailoverEnabled()) {
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
        Preconditions.checkState(!inited);
        this.keyParser = Objs.useValueIfNull(keyParser, this.keyParser);
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


    public List<DataSourceKey> allKeys() {
        return Collects.asList(dataSourceRegistry.keySet());
    }

    @Override
    public void unregister(DataSourceKey key) {
        dataSourceRegistry.remove(key);
    }

    @Override
    public boolean contains(DataSourceKey key) {
        return dataSourceRegistry.containsKey(key);
    }

    /**
     * 对数据源进行健康检查
     *
     * @param dataSource 要检查的数据源
     * @return 是否正常
     * @since 3.4.5
     */
    private boolean checkHealth(DataSource dataSource) {
        boolean success = true;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        boolean isAutoCommit = true;
        try {
            connection = dataSource.getConnection();
            isAutoCommit = connection.getAutoCommit();
            statement = connection.createStatement();
            String validationQuery = "select 1";
            if (dataSource instanceof NamedDataSource) {
                DataSourceProperties dataSourceProperties = ((NamedDataSource) dataSource).getDataSourceProperties();
                if (dataSourceProperties != null) {
                    validationQuery = Strings.useValueIfBlank(dataSourceProperties.getValidationQuery(), validationQuery);
                }
            }
            resultSet = statement.executeQuery(validationQuery);
            if (!isAutoCommit) {
                connection.commit();
            }
        } catch (SQLException exception) {
            logger.error(exception.getMessage(), exception);
            success = false;
        } catch (Throwable ex) {
            success = false;
        } finally {
            if (!success && connection != null && !isAutoCommit) {
                try {
                    connection.rollback();
                } catch (Throwable ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
        }

        if (resultSet != null) {
            IOs.close(resultSet);
        }
        if (statement != null) {
            IOs.close(statement);
        }
        if (connection != null) {
            IOs.close(connection);
        }
        return success;
    }

    private class HealthCheck implements Runnable {
        private NamedDataSource dataSource;

        HealthCheck(NamedDataSource namedDataSource) {
            this.dataSource = namedDataSource;
        }

        @Override
        public void run() {
            boolean health = checkHealth(dataSource);
            if (health) {
                failKeys.remove(dataSource.getDataSourceKey());
            } else {
                failKeys.add(dataSource.getDataSourceKey());
            }
        }
    }
}
