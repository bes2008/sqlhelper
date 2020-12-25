package com.jn.sqlhelper.datasource.key;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.registry.Registry;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.function.Predicate2;
import com.jn.langx.util.struct.Holder;
import com.jn.sqlhelper.datasource.key.parser.DataSourceAnnotationParser;
import com.jn.sqlhelper.datasource.key.parser.DataSourceKeyAnnotationParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 该类有两个作用：
 * 1）自动的解析出要执行的方法是在哪个数据源上。执行过程：对于要执行的方法，解析它或者它所在的类，或者接口上上声明的用于决定使用哪种数据源的注解。
 * 2）对解析过的方法，进行cache, 避免二次解析。
 */
public class MethodDataSourceKeyRegistry implements Registry<Method, DataSourceKey> {

    /**
     * 这里的 DataSourceKey，可以是一个确切的 key，也可以是个 keyPattern。
     * 不论是确切的key，还是个keyPattern，在DataSourceRegister里进行pattern匹配的。
     */
    private final ConcurrentHashMap<Method, Holder<DataSourceKey>> methodDataSourceKeyCache = new ConcurrentHashMap<Method, Holder<DataSourceKey>>();

    /**
     * parser 缓存
     */
    private Map<Class<? extends Annotation>, DataSourceKeyAnnotationParser> annotationParserMap = new LinkedHashMap<Class<? extends Annotation>, DataSourceKeyAnnotationParser>();

    public MethodDataSourceKeyRegistry() {
        registerDataSourceKeyParser(new DataSourceAnnotationParser());
    }

    @Override
    public void register(DataSourceKey dataSourceKeyHolder) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void register(Method method, DataSourceKey dataSourceKey) {
        Preconditions.checkNotNull(dataSourceKey);
        methodDataSourceKeyCache.putIfAbsent(method, new Holder<DataSourceKey>(dataSourceKey));
    }

    public void registerDataSourceKeyParser(DataSourceKeyAnnotationParser dataSourceKeyAnnotationParser) {
        if (dataSourceKeyAnnotationParser != null && dataSourceKeyAnnotationParser.getAnnotation() != null) {
            annotationParserMap.put(dataSourceKeyAnnotationParser.getAnnotation(), dataSourceKeyAnnotationParser);
        }
    }

    @Override
    @NonNull
    public DataSourceKey get(final Method method) {
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
        return holder.get();
    }
}
