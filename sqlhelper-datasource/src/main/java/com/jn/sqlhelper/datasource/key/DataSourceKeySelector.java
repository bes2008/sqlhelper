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

package com.jn.sqlhelper.datasource.key;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.annotation.Singleton;
import com.jn.langx.invocation.MethodInvocation;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.collection.multivalue.CommonMultiValueMap;
import com.jn.langx.util.collection.multivalue.MultiValueMap;
import com.jn.langx.util.collection.stack.ListableStack;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.function.Supplier;
import com.jn.langx.util.function.Supplier0;
import com.jn.langx.util.struct.Holder;
import com.jn.langx.util.struct.ThreadLocalHolder;
import com.jn.sqlhelper.datasource.DataSourceRegistry;
import com.jn.sqlhelper.datasource.DataSourceRegistryAware;
import com.jn.sqlhelper.datasource.NamedDataSource;
import com.jn.sqlhelper.datasource.key.router.AbstractDataSourceKeyRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Singleton
public class DataSourceKeySelector implements DataSourceRegistryAware {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceKeySelector.class);
    private static final ThreadLocalHolder<ListableStack<DataSourceKey>> DATA_SOURCE_KEY_HOLDER = new ThreadLocalHolder<ListableStack<DataSourceKey>>(
            new Supplier0<ListableStack<DataSourceKey>>() {
                @Override
                public ListableStack<DataSourceKey> get() {
                    return new ListableStack<DataSourceKey>();
                }
            });

    private static final ThreadLocalHolder<DataSourceKey> CURRENT_SELECTED = new ThreadLocalHolder<DataSourceKey>();

    @NonNull
    private DataSourceKeyRegistry dataSourceKeyRegistry;
    @NonNull
    private DataSourceRegistry dataSourceRegistry;


    /**
     * 是否开启故障转移功能
     */
    private volatile boolean failover = true;

    /**
     * 故障的key
     */
    private Set<DataSourceKey> failKeys = new CopyOnWriteArraySet<DataSourceKey>();

    // 初始化阶段初始化，后续只是使用
    private MultiValueMap<String, AbstractDataSourceKeyRouter> groupToRoutersMap = new CommonMultiValueMap<String, AbstractDataSourceKeyRouter>(new ConcurrentHashMap<String, Collection<AbstractDataSourceKeyRouter>>(), new Supplier<String, Collection<AbstractDataSourceKeyRouter>>() {
        @Override
        public Collection<AbstractDataSourceKeyRouter> get(String group) {
            return Collects.emptyArrayList();
        }
    });

    public void setDataSourceRegistry(DataSourceRegistry registry) {
        this.dataSourceRegistry = registry;
    }

    public DataSourceKeyRegistry getDataSourceKeyRegistry() {
        return dataSourceKeyRegistry;
    }

    public void setDataSourceKeyRegistry(DataSourceKeyRegistry dataSourceKeyRegistry) {
        this.dataSourceKeyRegistry = dataSourceKeyRegistry;
    }

    public void registerRouter(final AbstractDataSourceKeyRouter router) {
        Preconditions.checkNotNull(router);
        List<String> groups = Pipeline.of(router.getApplyGroups()).clearNulls().asList();
        Collects.forEach(groups, new Consumer<String>() {
            @Override
            public void accept(String group) {
                groupToRoutersMap.add(group, router);
            }
        });
    }

    public void registerRouters(List<AbstractDataSourceKeyRouter> routers) {
        Collects.forEach(routers, new Consumer<AbstractDataSourceKeyRouter>() {
            @Override
            public void accept(AbstractDataSourceKeyRouter router) {
                registerRouter(router);
            }
        });
    }

    /**
     * 当进入具有 DataSourceKey 定义的方法时调用
     */
    public static void addChoice(DataSourceKey key) {
        Preconditions.checkNotNull(key);
        ListableStack<DataSourceKey> stack = DATA_SOURCE_KEY_HOLDER.get();
        stack.push(key);
    }

    /**
     * 当离开具有 DataSourceKey 定义的方法时调用
     */
    public static void removeChoice(@Nullable DataSourceKey key) {
        ListableStack<DataSourceKey> stack = DATA_SOURCE_KEY_HOLDER.get();
        if (!stack.isEmpty()) {
            if (key != null) {
                if (key == stack.peek()) {
                    stack.pop();
                } else {
                    logger.warn("the datasource key {} will been removed is not equals the stack top :{}", key, stack.peek());
                }
            } else {
                stack.pop();
            }
        }
    }

    public static ListableStack<DataSourceKey> getChoices() {
        return DATA_SOURCE_KEY_HOLDER.get();
    }

    /**
     * 当离开根方法时调用
     */
    public static void clearChoices() {
        ListableStack<DataSourceKey> stack = DATA_SOURCE_KEY_HOLDER.get();
        stack.clear();
    }

    /**
     * 在最里层的， 最直接的使用DataSource 的方法调用后执行该方法
     */
    public static void setCurrent(DataSourceKey key) {
        Preconditions.checkNotNull(key);
        CURRENT_SELECTED.set(key);
        addChoice(key);
    }

    public static DataSourceKey getCurrent() {
        return CURRENT_SELECTED.get();
    }

    /**
     * 在最里层的， 最直接的使用DataSource 的方法调用后执行该方法
     */
    public static void removeCurrent() {
        DataSourceKey current = getCurrent();
        CURRENT_SELECTED.reset();
        removeChoice(current);
    }

    /**
     * 在真正的调用的地方调用即可
     *
     * @param router
     * @param methodInvocation
     * @return
     */
    public final DataSourceKey select(@Nullable AbstractDataSourceKeyRouter router, @Nullable final MethodInvocation methodInvocation) {
        DataSourceKey key = null;
        if (methodInvocation != null) {
            key = this.dataSourceKeyRegistry.get(methodInvocation.getJoinPoint());
        }
        if (key != null) {
            NamedDataSource dataSource = dataSourceRegistry.get(key);
            if (dataSource != null) {
                DataSourceKeySelector.setCurrent(key);
            }
        }
        key = DataSourceKeySelector.getCurrent();
        if (key == null) {
            key = doSelect(router, methodInvocation);
            if (key != null) {
                DataSourceKeySelector.setCurrent(key);
            }
        }
        if (key != null) {
            NamedDataSource dataSource = dataSourceRegistry.get(key);
            if (dataSource == null) {
                logger.warn("Can't find a datasource named: {}", key);
            }
        }
        return key;
    }

    /**
     * 指定的group下，选择某个datasource, 返回的是该组下的匹配到的 datasource key
     * <p>
     * 这里面不能去设置CURRENT_SELECTED
     */
    protected DataSourceKey doSelect(@Nullable AbstractDataSourceKeyRouter router, @Nullable final MethodInvocation methodInvocation) {
        Preconditions.checkArgument(dataSourceRegistry.size() > 0, "has no any datasource registered");
        if (dataSourceRegistry.size() == 1) {
            // 此情况下，不会去考虑DataSource是否出现故障了
            return dataSourceRegistry.getPrimary();
        }
        // 从线程栈里过滤
        ListableStack<DataSourceKey> stack = DATA_SOURCE_KEY_HOLDER.get();
        if (Emptys.isEmpty(stack)) {
            return dataSourceRegistry.getPrimary();
        }

        if (!CURRENT_SELECTED.isNull()) {
            return getCurrent();
        }

        final Holder<List<DataSourceKey>> dataSourceKeyList = new Holder<List<DataSourceKey>>();
        // 遍历 stack
        Collects.forEach(stack, new Predicate<DataSourceKey>() {
            @Override
            public boolean test(DataSourceKey dataSourceKey) {
                return dataSourceKey != null;
            }
        }, new Consumer<DataSourceKey>() {
            @Override
            public void accept(DataSourceKey dataSourceKey) {
                List<DataSourceKey> matched = dataSourceRegistry.findKeys(dataSourceKey);
                if (Emptys.isNotEmpty(matched)) {

                    if (failover) {
                        matched = Pipeline.of(matched).filter(new Predicate<DataSourceKey>() {
                            @Override
                            public boolean test(DataSourceKey dataSourceKey) {
                                return !failKeys.contains(dataSourceKey);
                            }
                        }).asList();
                    }

                    if (Emptys.isNotEmpty(matched)) {
                        dataSourceKeyList.set(matched);
                    }
                }
            }
        }, new Predicate<DataSourceKey>() {
            @Override
            public boolean test(DataSourceKey dataSourceKey) {
                return !dataSourceKeyList.isEmpty();
            }
        });

        if (!dataSourceKeyList.isEmpty()) {
            // keys 必然是同一个group下的
            final List<DataSourceKey> keys = dataSourceKeyList.get();
            if (keys.size() == 1) {
                return keys.get(0);
            }

            // 如果匹配到的过多，则进行二次过滤
            final Holder<DataSourceKey> keyHolder = new Holder<DataSourceKey>();
            if (router != null) {
                keyHolder.set(router.apply(keys, methodInvocation));
            }
            if (!keyHolder.isNull()) {
                return keyHolder.get();
            }

            // 指定的参数过滤不到的情况下，则基于 group router
            Collection<AbstractDataSourceKeyRouter> routers = groupToRoutersMap.get(keys.get(0).getGroup());

            Collects.forEach(routers, new Consumer<AbstractDataSourceKeyRouter>() {
                @Override
                public void accept(AbstractDataSourceKeyRouter groupRouter) {
                    keyHolder.set(groupRouter.apply(keys, methodInvocation));
                }
            }, new Predicate<AbstractDataSourceKeyRouter>() {
                @Override
                public boolean test(AbstractDataSourceKeyRouter filter) {
                    return !keyHolder.isNull();
                }
            });

            return keyHolder.get();
        }

        return null;
    }

    public DataSourceRegistry getDataSourceRegistry() {
        return dataSourceRegistry;
    }

    public boolean isFailover() {
        return failover;
    }

    public void setFailover(boolean failover) {
        this.failover = failover;
    }
}
