package com.jn.sqlhelper.mybatis.spring;

import com.jn.langx.util.collection.Collects;
import com.jn.sqlhelper.datasource.key.DataSourceKey;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

public class DynamicMapper<MAPPER> implements InvocationHandler {
    private Map<DataSourceKey, MAPPER> delegateMapperMap = Collects.<DataSourceKey, MAPPER>emptyHashMap();

    public DynamicMapper(Map<DataSourceKey, MAPPER> delegateMapperMap){
        this.delegateMapperMap.putAll(delegateMapperMap);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(getMapperDelegate(), args);
    }

    private Object getMapperDelegate() {
        DataSourceKey key = getDataSourceKey();
        return delegateMapperMap.get(key);
    }

    private DataSourceKey getDataSourceKey() {
        return null;
    }
}
