/*
 * Copyright 2021 the original author or authors.
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

package com.jn.sqlhelper.mybatis.spring.session.factory.dynamicdatasource;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.invocation.GenericMethodInvocation;
import com.jn.langx.invocation.MethodInvocation;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.reflect.Reflects;
import com.jn.sqlhelper.datasource.NamedDataSource;
import com.jn.sqlhelper.datasource.key.DataSourceKey;
import com.jn.sqlhelper.datasource.key.MethodInvocationDataSourceKeySelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger logger = LoggerFactory.getLogger(DynamicMapper.class);
    /**
     * 值为按照 Mybatis的规则创建的 mapper proxy对象.
     * 该字段在DynamicMapper创建时即完成，后面只是使用，所以不存在并发修改现象，故而只需要普通的map。
     */
    @NonNull
    private Class<MAPPER> mapperInterface;
    @NonNull
    private Map<DataSourceKey, MAPPER> delegateMapperMap = Collects.<DataSourceKey, MAPPER>emptyHashMap();
    @NonNull
    private MethodInvocationDataSourceKeySelector selector;

    public DynamicMapper(Class<MAPPER> mapperInterface, Map<DataSourceKey, MAPPER> delegateMapperMap, MethodInvocationDataSourceKeySelector selector) {
        this.mapperInterface = mapperInterface;
        this.delegateMapperMap.putAll(delegateMapperMap);
        setSelector(selector);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodInvocation methodInvocation = new GenericMethodInvocation(proxy, proxy, method, args);
        DataSourceKey key = null;
        try {
            Object mapper = getMapperDelegate(methodInvocation);
            key = MethodInvocationDataSourceKeySelector.getCurrent();
            if (mapper == null) {
                logger.error("Can't find a mybatis mapper for {}", key);
            }
            Object obj = method.invoke(mapper, args);
            return obj;
        } catch (Throwable ex) {
            logger.error("method {} call error: {}, ", method.getName(), ex.getMessage(), ex);
            throw ex;
        } finally {
            MethodInvocationDataSourceKeySelector.removeCurrent();
        }
    }

    private Object getMapperDelegate(MethodInvocation methodInvocation) {
        DataSourceKey key = selector.getDataSourceKeyRegistry().get(methodInvocation.getJoinPoint());
        if (key != null) {
            NamedDataSource dataSource = selector.getDataSourceRegistry().get(key);
            if (dataSource != null) {
                MethodInvocationDataSourceKeySelector.setCurrent(key);
            }
        }
        key = MethodInvocationDataSourceKeySelector.getCurrent();
        if (key == null) {
            key = selector.select(methodInvocation);
            if (key != null) {
                MethodInvocationDataSourceKeySelector.setCurrent(key);
            }
        }
        if (key == null) {
            throw new IllegalStateException(StringTemplates.formatWithPlaceholder("Can't find a suitable jdbc datasource for method: {}", Reflects.getMethodString(methodInvocation.getJoinPoint())));
        }
        return delegateMapperMap.get(key);
    }

    public void setSelector(MethodInvocationDataSourceKeySelector selector) {
        this.selector = selector;
    }
}
