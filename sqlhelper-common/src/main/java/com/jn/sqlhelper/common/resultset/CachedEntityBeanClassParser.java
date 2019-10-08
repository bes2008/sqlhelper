package com.jn.sqlhelper.common.resultset;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CachedEntityBeanClassParser extends EntityBeanClassParser {
    private static final CachedEntityBeanClassParser instance = new CachedEntityBeanClassParser();

    private ConcurrentHashMap<Class, Map<String, EntityFieldInfo>> cache = new ConcurrentHashMap<Class, Map<String, EntityFieldInfo>>();

    private CachedEntityBeanClassParser() {

    }

    public static CachedEntityBeanClassParser getInstance() {
        return instance;
    }

    @Override
    public Map<String, EntityFieldInfo> parse(Class clazz) {
        if (cache.containsKey(clazz)) {
            return cache.get(clazz);
        }
        Map<String, EntityFieldInfo> entityFieldInfoMap = super.parse(clazz);
        cache.putIfAbsent(clazz, entityFieldInfoMap);
        return entityFieldInfoMap;
    }
}
