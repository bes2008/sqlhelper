package com.jn.agileway.jdbc.datasource;

import com.jn.langx.annotation.Name;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.OnClasses;
import com.jn.langx.factory.Provider;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

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

                if (Reflects.isAnnotationPresent(dsClass, Name.class)) {
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
    }

    @Override
    public DataSourceFactory get(String implementationKey) {
        return registry.get(implementationKey);
    }

    public DataSourceFactory get() {
        DataSourceFactory dataSourceFactory = registry.get(DataSources.DATASOURCE_IMPLEMENT_KEY_HIKARICP);
        if (dataSourceFactory == null) {
            dataSourceFactory = Collects.findFirst(registry.values());
        }
        return dataSourceFactory;
    }

}
