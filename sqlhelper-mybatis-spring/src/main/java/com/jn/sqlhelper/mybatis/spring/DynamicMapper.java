package com.jn.sqlhelper.mybatis.spring;

import com.jn.langx.util.collection.Collects;
import com.jn.sqlhelper.datasource.key.DataSourceKey;
import com.jn.sqlhelper.datasource.key.DataSourceKeySelector;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * MyBatis 会为每一个 Mapper 接口创建一个代理对象。
 * 对于多源场景，我们选择为每一个数据源都创建一个Mapper对象后，将这些 Mapper再进行整合为一个 map结构
 * 也就是现在看到的 DynamicMapper
 *
 * @param <MAPPER>
 */
public class DynamicMapper<MAPPER> implements InvocationHandler {
    /**
     * 值为按照 Mybatis的规则创建的 mapper proxy对象.
     * 该字段在DynamicMapper创建时即完成，后面只是使用，所以不存在并发修改现象，故而只需要普通的map。
     */
    private Class<MAPPER> mapperInterface;
    private Map<DataSourceKey, MAPPER> delegateMapperMap = Collects.<DataSourceKey, MAPPER>emptyHashMap();

    public DynamicMapper(Class<MAPPER> mapperInterface, Map<DataSourceKey, MAPPER> delegateMapperMap) {
        this.mapperInterface = mapperInterface;
        this.delegateMapperMap.putAll(delegateMapperMap);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(getMapperDelegate(method), args);
    }

    private Object getMapperDelegate(Method method) {
        DataSourceKey key = DataSourceKeySelector.getCurrent();
        return delegateMapperMap.get(key);
    }
}
