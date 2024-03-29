/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-2.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jn.sqlhelper.datasource.factory;

import com.jn.langx.Provider;
import com.jn.langx.annotation.*;
import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.util.ClassLoaders;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.function.Supplier0;
import com.jn.langx.util.reflect.Reflects;
import com.jn.sqlhelper.datasource.DataSources;
import com.jn.sqlhelper.datasource.key.DataSourceKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public final class DataSourceFactoryProvider implements Provider<String, DataSourceFactory>, Supplier0<DataSourceFactory>, Initializable {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceFactoryProvider.class);
    private static final ConcurrentHashMap<String, DataSourceFactory> registry = new ConcurrentHashMap<String, DataSourceFactory>();

    private static final DataSourceFactoryProvider INSTANCE = new DataSourceFactoryProvider();

    private DataSourceFactoryProvider() {
        init();
    }

    public void init() {
        ServiceLoader<DataSourceFactory> factoryLoader = ServiceLoader.load(DataSourceFactory.class);
        Collects.forEach(factoryLoader, new Consumer<DataSourceFactory>() {
            @Override
            public void accept(DataSourceFactory dataSourceFactory) {
                if (dataSourceFactory == null) {
                    return;
                }
                Class<? extends DataSourceFactory> dsClass = dataSourceFactory.getClass();

                if (!Reflects.isAnnotationPresent(dsClass, Name.class)) {
                    logger.warn("Couldn't found the annotation com.jn.langx.annotation.@Name at the class {}", Reflects.getFQNClassName(dsClass));
                    return;
                }
                Name nameAnno = Reflects.getAnnotation(dsClass, Name.class);
                String implementationKey = nameAnno.value();
                if (Emptys.isEmpty(implementationKey)) {
                    logger.warn("Couldn't found a valid annotation com.jn.langx.annotation.@Name at the class {}, the name is null or empty", Reflects.getFQNClassName(dsClass));
                }

                OnClasses onClasses = Reflects.getAnnotation(dsClass, OnClasses.class);
                boolean available = true;
                if (onClasses != null && Emptys.isNotEmpty(onClasses.value())) {
                    String[] requiredDependencyClasses = onClasses.value();
                    if (!Collects.allMatch(new Predicate<String>() {
                        @Override
                        public boolean test(String dependencyClass) {
                            return ClassLoaders.hasClass(dependencyClass, DataSourceFactoryProvider.class.getClassLoader());
                        }
                    }, requiredDependencyClasses)) {
                        available = false;
                    }
                }

                if (available) {
                    register(implementationKey, dataSourceFactory);
                } else {
                    logger.warn("Couldn't found the key classes : {}", Strings.join(",", onClasses.value()));
                }
            }
        });

    }

    public static DataSourceFactoryProvider getInstance() {
        return INSTANCE;
    }

    public void register(@NonNull String implementationKey, @NonNull DataSourceFactory dataSourceFactory) {
        Preconditions.checkNotNull(implementationKey, "the jdbc datasource implementation key is empty or null");
        Preconditions.checkNotNull(implementationKey, "the jdbc datasource factory key is null");
        registry.putIfAbsent(implementationKey, dataSourceFactory);
        logger.info("Register a jdbc dataSource factory: {}, {}", implementationKey, Reflects.getFQNClassName(dataSourceFactory.getClass()));
    }

    @Override
    public DataSourceFactory get(String implementationKey) {
        Preconditions.checkNotNull(implementationKey, "the implementation is null");
        return registry.get(implementationKey);
    }

    public DataSourceFactory get() {
        DataSourceFactory dataSourceFactory = registry.get(DataSources.DATASOURCE_IMPLEMENT_KEY_HIKARICP);
        if (dataSourceFactory == null) {
            dataSourceFactory = Collects.findFirst(registry.values());
        }
        return dataSourceFactory;
    }

    public DataSourceFactory findSuitableDataSourceFactory(@Nullable String implementationKey) {
        return findSuitableDataSourceFactory(implementationKey, null);
    }

    public DataSourceFactory findSuitableDataSourceFactory(@Nullable String implementationKey, @Nullable DataSourceKey key) {
        DataSourceFactory delegate = null;

        if (Emptys.isEmpty(implementationKey)) {
            if (key != null) {
                logger.warn("The 'implementation' property is not set for your jdbc datasource {}, so will select automation", key);
            } else {
                logger.warn("The 'implementation' property is not set for one of your jdbc datasources, so will select automation");
            }
        } else {
            delegate = this.get(implementationKey);
            if (delegate == null) {
                logger.warn("Can't find the jdbc database factory: {}, so will select automation", implementationKey);
            }
        }
        if (delegate == null) {
            delegate = this.get();
        }
        if (delegate == null) {
            logger.error("Can't find any jdbc database factory");
            throw new IllegalStateException("Can't find any jdbc database factory, please check has any the supported database implementations in your classpath: " + IMPLEMENTATION_JARS);
        }

        return delegate;
    }

    private static final String IMPLEMENTATION_JARS = "\n" +
            "\t1) HikariCP.jar" +
            "\t2) tomcat-jdbc.jar" +
            "\t3) commons-dbcp2.jar" +
            "\t4) druid.jar" +
            "\t5) c3p0.jar";
}
