package com.jn.sqlhelper.datasource;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.invocation.proxy.Proxys;
import com.jn.langx.util.Preconditions;
import com.jn.sqlhelper.datasource.key.DataSourceKey;
import com.jn.sqlhelper.datasource.key.MethodInvocationDataSourceKeySelector;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * apache-dbutil, spring-jdbc 等这类简单的工具，可以用这种方式来处理多数据源，甚至可以不用，完全自己来管理就行。
 * mybatis时，不建议用这种方式，因为有些场景下的问题解决不掉。 mybatis 下，推荐使用 com.jn.sqlhelper.mybatis.spring.datasource.DynamicMapper来解决。
 *
 * <pre>
 *     SimpleDynamicDataSourceInvocationHandler handler = new SimpleDynamicDataSourceInvocationHandler();
 *     handler.setDataSourceRegistry(registry);
 *     Proxy.newProxyInstance(classLoader, handler, new Class[]{DataSource.class});
 * </pre>
 * @since 3.4.0
 */
public class SimpleDynamicDataSourceInvocationHandler implements InvocationHandler, DataSourceRegistryAware {
    private DataSourceRegistry registry;

    @Override
    public void setDataSourceRegistry(DataSourceRegistry registry) {
        this.registry = registry;
    }

    private DataSource getDelegateDataSource() {
        return getDelegateDataSource(true);
    }

    private DataSource getDelegateDataSource(boolean throwIfNotFound) {
        DataSourceKey key = MethodInvocationDataSourceKeySelector.getCurrent();
        if (key == null) {
            key = registry.getPrimary();
        }
        if (throwIfNotFound) {
            Preconditions.checkNotNull(key, "the jdbc datasource key is null");
        }
        DataSource dataSource = registry.get(key);
        if (throwIfNotFound) {
            Preconditions.checkNotNull(dataSource, "can't find a suitable jdbc datasource");
        }
        return dataSource;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        DataSource dataSource = getDelegateDataSource();
        return method.invoke(dataSource, args);
    }

    public static DataSource newDynamicDataSource(@NonNull DataSourceRegistry registry) {
        SimpleDynamicDataSourceInvocationHandler handler = new SimpleDynamicDataSourceInvocationHandler();
        handler.setDataSourceRegistry(registry);
        return (DataSource) Proxys.newProxyInstance(handler, new Class[]{DataSource.class});
    }
}
