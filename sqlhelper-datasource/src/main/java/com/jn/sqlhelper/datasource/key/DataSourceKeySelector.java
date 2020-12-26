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

import com.jn.langx.algorithm.loadbalance.LoadBalancer;
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
import com.jn.langx.util.function.*;
import com.jn.langx.util.struct.Holder;
import com.jn.langx.util.struct.ThreadLocalHolder;
import com.jn.sqlhelper.datasource.DataSourceRegistry;
import com.jn.sqlhelper.datasource.DataSourceRegistryAware;
import com.jn.sqlhelper.datasource.NamedDataSource;
import com.jn.sqlhelper.datasource.key.router.DataSourceKeyRouter;
import com.jn.sqlhelper.datasource.key.router.RandomRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于处理负载均衡
 * 该类的职责：
 * 1、添加、释放当前业务处理的可选 DataSourceKeys
 * 2、在真正的数据源调用时，筛选合适的数据源
 */
@Singleton
public class DataSourceKeySelector implements DataSourceRegistryAware, LoadBalancer<DataSourceKey, MethodInvocation> {
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
    private MethodDataSourceKeyRegistry dataSourceKeyRegistry;
    @NonNull
    private DataSourceRegistry dataSourceRegistry;


    private DataSourceKeyRouter defaultRouter;
    /**
     * 缓存所有的Router。
     * key: route name
     * value: router
     */
    private ConcurrentHashMap<String, DataSourceKeyRouter> routerMap = new ConcurrentHashMap<String, DataSourceKeyRouter>();
    /**
     * 缓存 group所用的 routers
     * key: group
     */
    private MultiValueMap<String, String> groupToRoutersMap = new CommonMultiValueMap<String, String>(new ConcurrentHashMap<String, Collection<String>>(), new Supplier<String, Collection<String>>() {
        @Override
        public Collection<String> get(String group) {
            return Collects.newArrayList();
        }
    });

    public DataSourceKeySelector() {
        RandomRouter r = new RandomRouter();
        r.setLoadBalancer(this);
        registerRouter(r, true);
    }

    public void setDataSourceRegistry(DataSourceRegistry registry) {
        this.dataSourceRegistry = registry;
        this.dataSourceRegistry.setLoadBalancer(this);
    }

    public MethodDataSourceKeyRegistry getDataSourceKeyRegistry() {
        return dataSourceKeyRegistry;
    }

    public void setDataSourceKeyRegistry(MethodDataSourceKeyRegistry dataSourceKeyRegistry) {
        this.dataSourceKeyRegistry = dataSourceKeyRegistry;
    }

    public void setDefaultRouter(DataSourceKeyRouter router) {
        this.defaultRouter = router;
    }

    public void registerRouter(final DataSourceKeyRouter router) {
        registerRouter(router, false);
    }

    public void registerRouter(final DataSourceKeyRouter router, boolean asDefault) {
        Preconditions.checkNotNull(router);
        Preconditions.checkNotEmpty(router.getName(), "the router name is null or empty");
        routerMap.put(router.getName(), router);
        router.setLoadBalancer(this);
        if (asDefault) {
            setDefaultRouter(router);
        }
    }

    public void registerRouters(List<DataSourceKeyRouter> routers) {
        Collects.forEach(routers, new Consumer<DataSourceKeyRouter>() {
            @Override
            public void accept(DataSourceKeyRouter router) {
                registerRouter(router);
            }
        });
    }


    /**
     * 为 group 划分 routers
     */
    public void allocateRouters(final String group, Collection<String> routerNames) {
        Collects.forEach(routerNames, new Consumer<String>() {
            @Override
            public void accept(String routerName) {
                groupToRoutersMap.addIfAbsent(group, routerName);
            }
        });
    }

    public List<DataSourceKeyRouter> getRouters(String group) {
        return Pipeline.of(groupToRoutersMap.get(group)).map(new Function<String, DataSourceKeyRouter>() {
            @Override
            public DataSourceKeyRouter apply(String routerName) {
                return routerMap.get(routerName);
            }
        }).clearNulls().asList();
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
     */
    public final DataSourceKey select(@Nullable final MethodInvocation methodInvocation) {
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
            key = doSelect(methodInvocation);
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
    protected DataSourceKey doSelect(@Nullable final MethodInvocation methodInvocation) {
        if (!CURRENT_SELECTED.isNull()) {
            return getCurrent();
        }

        Preconditions.checkArgument(dataSourceRegistry.size() > 0, "has no any datasource registered");
        if (dataSourceRegistry.size() == 1) {
            // 此情况下，不会去考虑DataSource是否出现故障了
            return dataSourceRegistry.getPrimary();
        }

        final Holder<List<DataSourceKey>> dataSourceKeyList = new Holder<List<DataSourceKey>>();

        if (dataSourceKeyList.isEmpty()) {
            // 从线程栈里过滤
            ListableStack<DataSourceKey> stack = DATA_SOURCE_KEY_HOLDER.get();
            if (Emptys.isEmpty(stack)) {
                List<DataSourceKey> matched = dataSourceRegistry.findKeys(dataSourceRegistry.getPrimary());
                if (Emptys.isNotEmpty(matched)) {
                    dataSourceKeyList.set(matched);
                }
            } else {
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
                            dataSourceKeyList.set(matched);
                        }
                    }
                }, new Predicate<DataSourceKey>() {
                    @Override
                    public boolean test(DataSourceKey dataSourceKey) {
                        return !dataSourceKeyList.isEmpty();
                    }
                });
            }
        }
        if (!dataSourceKeyList.isEmpty()) {
            // keys 必然是同一个group下的
            final List<DataSourceKey> keys = dataSourceKeyList.get();
            if (keys.size() == 1) {
                return keys.get(0);
            }

            // 如果匹配到的过多，则进行二次过滤
            final Holder<DataSourceKey> keyHolder = new Holder<DataSourceKey>();
            // 指定的参数过滤不到的情况下，则基于 group router
            Collection<DataSourceKeyRouter> routers = getRouters(keys.get(0).getGroup());
            if (Emptys.isEmpty(routers) && defaultRouter != null) {
                routers = Collects.asList(defaultRouter);
            }
            Collects.forEach(routers, new Consumer<DataSourceKeyRouter>() {
                @Override
                public void accept(DataSourceKeyRouter groupRouter) {
                    keyHolder.set(groupRouter.select(keys, methodInvocation));
                }
            }, new Predicate<DataSourceKeyRouter>() {
                @Override
                public boolean test(DataSourceKeyRouter groupRouter) {
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

    @Override
    public void addNode(DataSourceKey node) {
        // NOOP
    }

    @Override
    public void removeNode(DataSourceKey key) {
        // NOOP
    }

    @Override
    public boolean hasNode(DataSourceKey key) {
        return dataSourceRegistry.get(key) != null;
    }

    @Override
    public void markDown(DataSourceKey key) {

    }

    @Override
    public List<DataSourceKey> getNodes() {
        return dataSourceRegistry.allKeys();
    }

    @Override
    public List<DataSourceKey> getNodes(Predicate<DataSourceKey> predicate) {
        return Pipeline.of(getNodes()).filter(predicate).asList();
    }

    @Override
    public boolean isEmpty() {
        return dataSourceRegistry.size() == 0;
    }
}
