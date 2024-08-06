package com.jn.sqlhelper.mybatisplus.spring.boot.autoconfigure;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.jn.langx.util.reflect.Reflects;

import java.lang.reflect.Method;
import java.util.HashSet;

public class MyBatisPlusPropertiesClone_LE_3_5_3 implements MyBatisPlusPropertiesClone{
    @Override
    public MybatisPlusProperties cloneObject(MybatisPlusProperties properties) {
        MybatisPlusProperties props = new MybatisPlusProperties();
        props.setCheckConfigLocation(properties.isCheckConfigLocation());
        props.setConfigLocation(properties.getConfigLocation());
        props.setConfigurationProperties(properties.getConfigurationProperties());
        props.setExecutorType(properties.getExecutorType());
        props.setMapperLocations(properties.getMapperLocations());
        props.setTypeAliasesPackage(properties.getTypeAliasesPackage());
        props.setTypeAliasesSuperType(properties.getTypeAliasesSuperType());
        props.setTypeEnumsPackage(properties.getTypeEnumsPackage());
        props.setTypeHandlersPackage(properties.getTypeHandlersPackage());

        GlobalConfig gc = properties.getGlobalConfig();
        GlobalConfig globalConfig = null;
        if (gc != null) {
            globalConfig = new GlobalConfig();
            globalConfig.setBanner(gc.isBanner());
            // mybatis-plus 3.5.0 中移除了 datacenterid 属性
            if(Reflects.getPublicMethod(GlobalConfig.class, "setDatacenterId", String.class)!=null){
                String datacenterid = Reflects.invokePublicMethod(gc,"getDatacenterId",new Class[0], new Object[0],true, true);
                Reflects.invokePublicMethod(globalConfig, "setDatacenterId", new Class[]{String.class}, new Object[]{datacenterid}, true, true);
                // globalConfig.setDatacenterId(gc.getDatacenterId());
            }

            globalConfig.setDbConfig(gc.getDbConfig());
            globalConfig.setEnableSqlRunner(gc.isEnableSqlRunner());
            // 该字段必须保证，每个数据源一份
            globalConfig.setMapperRegistryCache(new HashSet<String>());
            globalConfig.setMetaObjectHandler(gc.getMetaObjectHandler());
            globalConfig.setSqlInjector(gc.getSqlInjector());
            globalConfig.setSuperMapperClass(gc.getSuperMapperClass());
            // mybatis-plus 3.5.0 中移除了 workerId 属性
            if(Reflects.getPublicMethod(GlobalConfig.class, "setWorkerId", String.class)!=null){
                String datacenterid = Reflects.invokePublicMethod(gc,"getWorkerId",new Class[0], new Object[0],true, true);
                Reflects.invokePublicMethod(globalConfig, "setWorkerId", new Class[]{String.class}, new Object[]{datacenterid}, true, true);
                // globalConfig.setWorkerId(gc.getWorkerId());
            }

            // mybatis-plus高版本移除了 setSqlParserCache(), isSqlParserCache()
            // globalConfig.setSqlParserCache(gc.isSqlParserCache());

            props.setGlobalConfig(globalConfig);
        }

        MybatisConfiguration configurationPrototype = properties.getConfiguration();
        if (configurationPrototype != null) {
            MybatisConfiguration configuration = new MybatisConfiguration();
            if (globalConfig != null) {
                // mybatis-plus 3.5.3 中移除了 globalConfig 属性
                if(Reflects.getPublicMethod(MybatisConfiguration.class, "setGlobalConfig", GlobalConfig.class)!=null){
                    Reflects.invokePublicMethod(configuration, "setGlobalConfig", new Class[]{GlobalConfig.class}, new Object[]{globalConfig}, true, true);
                }
                //configuration.setGlobalConfig(globalConfig);

                // @since sqlhelper 4.0.3
                GlobalConfigUtils.setGlobalConfig(configuration, globalConfig);
            }

            configuration.setAggressiveLazyLoading(configurationPrototype.isAggressiveLazyLoading());
            configuration.setAutoMappingBehavior(configurationPrototype.getAutoMappingBehavior());
            configuration.setAutoMappingUnknownColumnBehavior(configurationPrototype.getAutoMappingUnknownColumnBehavior());

            configuration.setCacheEnabled(configurationPrototype.isCacheEnabled());
            configuration.setCallSettersOnNulls(configurationPrototype.isCallSettersOnNulls());
            configuration.setConfigurationFactory(configurationPrototype.getConfigurationFactory());

            configuration.setDatabaseId(configurationPrototype.getDatabaseId());
            configuration.setDefaultScriptingLanguage(configurationPrototype.getDefaultScriptingLanuageInstance().getClass());


            configuration.setDefaultExecutorType(configurationPrototype.getDefaultExecutorType());
            configuration.setDefaultFetchSize(configurationPrototype.getDefaultFetchSize());
            configuration.setDefaultStatementTimeout(configurationPrototype.getDefaultStatementTimeout());

            configuration.setJdbcTypeForNull(configurationPrototype.getJdbcTypeForNull());

            configuration.setLazyLoadingEnabled(configurationPrototype.isLazyLoadingEnabled());
            configuration.setLazyLoadTriggerMethods(configurationPrototype.getLazyLoadTriggerMethods());
            configuration.setLocalCacheScope(configurationPrototype.getLocalCacheScope());
            configuration.setLogImpl(configurationPrototype.getLogImpl());
            configuration.setLogPrefix(configurationPrototype.getLogPrefix());

            configuration.setMapUnderscoreToCamelCase(configurationPrototype.isMapUnderscoreToCamelCase());
            configuration.setMultipleResultSetsEnabled(configurationPrototype.isMultipleResultSetsEnabled());

            configuration.setObjectFactory(configurationPrototype.getObjectFactory());
            configuration.setObjectWrapperFactory(configurationPrototype.getObjectWrapperFactory());

            configuration.setProxyFactory(configurationPrototype.getProxyFactory());

            configuration.setReflectorFactory(configurationPrototype.getReflectorFactory());

            configuration.setSafeResultHandlerEnabled(configurationPrototype.isSafeResultHandlerEnabled());
            configuration.setSafeRowBoundsEnabled(configurationPrototype.isSafeRowBoundsEnabled());

            Method setUseGeneratedShortKey = Reflects.getPublicMethod(configuration.getClass(), "setUseGeneratedShortKey", boolean.class);
            if (setUseGeneratedShortKey != null) {
                boolean isUseGeneratedShortKey = Reflects.invokePublicMethod(configurationPrototype, "isUseGeneratedShortKey", new Class[0], new Object[0], true, false);
                Reflects.invoke(setUseGeneratedShortKey, configuration, new Object[]{isUseGeneratedShortKey}, true, false);
            }

            configuration.setUseColumnLabel(configurationPrototype.isUseColumnLabel());
            configuration.setUseGeneratedKeys(configurationPrototype.isUseGeneratedKeys());

            configuration.setVariables(configurationPrototype.getVariables());
            configuration.setVfsImpl(configurationPrototype.getVfsImpl());

            props.setConfiguration(configuration);
        }

        return props;
    }
}
