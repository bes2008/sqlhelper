package com.jn.sqlhelper.datasource.key;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.registry.Registry;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.function.Predicate2;
import com.jn.langx.util.struct.Holder;
import com.jn.sqlhelper.datasource.annotation.DataSource;
import com.jn.sqlhelper.datasource.key.parser.DataSourceAnnotationParser;
import com.jn.sqlhelper.datasource.key.parser.DataSourceKeyAnnotationParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataSourceKeyRegistry implements Registry<Method, Holder<DataSourceKey>> {

    private ConcurrentHashMap<Method, Holder<DataSourceKey>> methodDataSourceKeyCache = new ConcurrentHashMap<Method, Holder<DataSourceKey>>();
    private Map<Class<? extends Annotation>, DataSourceKeyAnnotationParser> annotationParserMap = new LinkedHashMap<Class<? extends Annotation>, DataSourceKeyAnnotationParser>();

    public DataSourceKeyRegistry() {
        annotationParserMap.put(DataSource.class, new DataSourceAnnotationParser());
    }

    @Override
    public void register(Holder<DataSourceKey> dataSourceKeyHolder) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void register(Method method, Holder<DataSourceKey> dataSourceKey) {
        methodDataSourceKeyCache.putIfAbsent(method, dataSourceKey);
    }

    @Override
    @NonNull
    public Holder<DataSourceKey> get(final Method method) {
        Holder<DataSourceKey> holder = methodDataSourceKeyCache.get(method);
        if (holder == null) {
            // 第一次用到该方法
            synchronized (this) {
                final Holder<DataSourceKey> holder0 = new Holder<DataSourceKey>();
                Collects.forEach(annotationParserMap, new Consumer2<Class<? extends Annotation>, DataSourceKeyAnnotationParser>() {
                    @Override
                    public void accept(Class<? extends Annotation> annotationClass, DataSourceKeyAnnotationParser parser) {
                        DataSourceKey key = parser.parse(method);
                        if (key != null) {
                            holder0.set(key);
                        }
                    }
                }, new Predicate2<Class<? extends Annotation>, DataSourceKeyAnnotationParser>() {
                    @Override
                    public boolean test(Class<? extends Annotation> key, DataSourceKeyAnnotationParser value) {
                        return !holder0.isNull();
                    }
                });
                methodDataSourceKeyCache.putIfAbsent(method, holder0);
                holder = methodDataSourceKeyCache.get(method);
            }
        }
        return holder;
    }
}
