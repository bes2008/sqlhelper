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

package com.jn.sqlhelper.mybatisplus.spring.boot.autoconfigure;


import com.baomidou.mybatisplus.autoconfigure.*;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.reflect.Reflects;
import com.jn.sqlhelper.datasource.DataSourceRegistry;
import com.jn.sqlhelper.datasource.NamedDataSource;
import com.jn.sqlhelper.datasource.key.MethodInvocationDataSourceKeySelector;
import com.jn.sqlhelper.datasource.supports.spring.boot.DynamicDataSourcesAutoConfiguration;
import com.jn.sqlhelper.mybatis.spring.session.factory.dynamicdatasource.DelegatingSqlSessionFactory;
import com.jn.sqlhelper.mybatis.spring.session.factory.dynamicdatasource.DynamicSqlSessionFactory;
import com.jn.sqlhelper.mybatis.spring.session.factory.dynamicdatasource.DynamicSqlSessionTemplate;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.MyBatisExceptionTranslator;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ListFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.support.PersistenceExceptionTranslator;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;

@ConditionalOnProperty(name = "sqlhelper.dynamic-datasource.enabled", havingValue = "true", matchIfMissing = false)
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class, DynamicSqlSessionFactory.class})
@ConditionalOnBean(name = "dataSourcesFactoryBean")
@EnableConfigurationProperties(MybatisPlusProperties.class)
@AutoConfigureAfter(DynamicDataSourcesAutoConfiguration.class)
@AutoConfigureBefore({MybatisPlusAutoConfiguration.class})
@Configuration
public class DynamicSqlSessionTemplateAutoConfiguration implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(DynamicSqlSessionTemplateAutoConfiguration.class);
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public MybatisPlusProperties cloneMybatisPlusProperties(@NonNull MybatisPlusProperties properties) {
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

    @Bean(name = "sqlSessionFactory")
    public DynamicSqlSessionFactory sqlSessionFactory(
            final ObjectProvider<DataSourceRegistry> registryProvider,
            @Qualifier("dataSourcesFactoryBean")
                    ListFactoryBean dataSourcesFactoryBean,
            final MybatisPlusProperties properties,
            final ObjectProvider<Interceptor[]> interceptorsProvider,
            final ObjectProvider<TypeHandler[]> typeHandlerProvider,
            final ObjectProvider<LanguageDriver[]> languageDriverProvider,
            final ResourceLoader resourceLoader,
            final ObjectProvider<DatabaseIdProvider> databaseIdProvider,
            final ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider,
            /**
             * 从sqlhelper 4.0.3 开始新加该参数，为了应对mybatis-plus 3.5.3。
             * 但是 mybatis-plus 3.5.3 版本才开始加这个接口
             */
            final ObjectProvider<List<SqlSessionFactoryBeanCustomizer>> sqlSessionFactoryBeanCustomizers,
            final ObjectProvider<List<MybatisPlusPropertiesCustomizer>> mybatisPlusPropertiesCustomizerProvider) throws BeanCreationException {
        List<DataSource> dataSources = null;
        try {
            List ds = dataSourcesFactoryBean.getObject();
            dataSources = (List<DataSource>) ds;
        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
        }
        if (Emptys.isNotEmpty(dataSources)) {
            try {
                registryProvider.getObject();
            } catch (BeansException ex) {
                logger.error("please check whether the sqlhelper-datasource.jar in the classpath or not");
                throw ex;
            }

            List<ConfigurationCustomizer> customizers = configurationCustomizersProvider.getIfAvailable();
            final ConfigurationCustomizer transactionFactoryCustomizer = Collects.findFirst(customizers, new Predicate<ConfigurationCustomizer>() {
                @Override
                public boolean test(ConfigurationCustomizer customizer) {
                    return customizer instanceof DynamicDataSourceTransactionFactoryCustomizer;
                }
            });


            final DynamicSqlSessionFactory dynamicSqlSessionFactory = new DynamicSqlSessionFactory();
            Collects.forEach(dataSources, new Consumer<DataSource>() {
                @Override
                public void accept(DataSource dataSource) {
                    NamedDataSource namedDataSource = registryProvider.getObject().wrap(dataSource);
                    try {
                        logger.info("===[SQLHelper & MyBatis-Plus 3.x]=== Create mybatis SqlSessionFactory instance for datasource {}", namedDataSource.getDataSourceKey());
                        MybatisPlusProperties newProperties = cloneMybatisPlusProperties(properties);
                        SqlSessionFactory delegate = createSqlSessionFactory(dataSource, newProperties, interceptorsProvider, typeHandlerProvider, languageDriverProvider, resourceLoader, databaseIdProvider,configurationCustomizersProvider,sqlSessionFactoryBeanCustomizers,  mybatisPlusPropertiesCustomizerProvider);
                        if (delegate != null) {

                            if (transactionFactoryCustomizer != null) {
                                transactionFactoryCustomizer.customize((MybatisConfiguration) delegate.getConfiguration());
                            }

                            DelegatingSqlSessionFactory sqlSessionFactory = new DelegatingSqlSessionFactory();
                            sqlSessionFactory.setDelegate(delegate);
                            PersistenceExceptionTranslator translator = new MyBatisExceptionTranslator(delegate.getConfiguration().getEnvironment().getDataSource(), true);
                            sqlSessionFactory.setPersistenceExceptionTranslator(translator);
                            dynamicSqlSessionFactory.addSqlSessionFactory(namedDataSource.getDataSourceKey(), sqlSessionFactory);
                        }
                    } catch (Throwable ex) {
                        logger.error("Error occur when create SqlSessionFactory for datasource {}, error: {}", namedDataSource.getDataSourceKey(), ex.getMessage(), ex);
                    }
                }
            });
            return dynamicSqlSessionFactory;
        } else {
            throw new BeanCreationException("Can't find any jdbc datasource");
        }

    }

    private SqlSessionFactory createSqlSessionFactory(DataSource dataSource,
                                                      MybatisPlusProperties properties,
                                                      ObjectProvider<Interceptor[]> interceptorsProvider,
                                                      ObjectProvider<TypeHandler[]> typeHandlerProvider,
                                                      ObjectProvider<LanguageDriver[]> languageDriverProvider,
                                                      ResourceLoader resourceLoader,
                                                      ObjectProvider<DatabaseIdProvider> databaseIdProviderObjectProvider,
                                                      ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider,
                                                      /**
                                                       * 从sqlhelper 4.0.3 开始新加该参数，为了应对mybatis-plus 3.5.3
                                                       */
                                                      ObjectProvider<List<SqlSessionFactoryBeanCustomizer>> sqlSessionFactoryBeanCustomizers,
                                                      ObjectProvider<List<MybatisPlusPropertiesCustomizer>> mybatisPlusPropertiesCustomizerProvider) throws Exception {

        MybatisPlusAutoConfiguration mybatisAutoConfiguration = null;

        // mybatis-plus-boot-starter 不同版本的构造器会有变化

        // mybatis-plus-boot-starter 3.1.2

        mybatisAutoConfiguration = Reflects.newInstance(MybatisPlusAutoConfiguration.class,
                new Class[]{
                        MybatisPlusProperties.class,
                        ObjectProvider.class,
                        ResourceLoader.class,
                        ObjectProvider.class,
                        ObjectProvider.class,
                        ObjectProvider.class,
                        ApplicationContext.class
                },
                new Object[]{
                        properties,
                        interceptorsProvider,
                        resourceLoader,
                        databaseIdProviderObjectProvider,
                        configurationCustomizersProvider,
                        mybatisPlusPropertiesCustomizerProvider,
                        this.applicationContext
                });

        // mybatis-plus-boot-starter 3.2.0 ~ 3.4.1
        if (mybatisAutoConfiguration == null) {
            mybatisAutoConfiguration = Reflects.newInstance(MybatisPlusAutoConfiguration.class,
                    new Class[]{
                            MybatisPlusProperties.class,
                            ObjectProvider.class,
                            ObjectProvider.class,
                            ObjectProvider.class,
                            ResourceLoader.class,
                            ObjectProvider.class,
                            ObjectProvider.class,
                            ObjectProvider.class,
                            ApplicationContext.class
                    },
                    new Object[]{
                            properties,
                            interceptorsProvider,
                            typeHandlerProvider,
                            languageDriverProvider,
                            resourceLoader,
                            databaseIdProviderObjectProvider,
                            configurationCustomizersProvider,
                            mybatisPlusPropertiesCustomizerProvider,
                            this.applicationContext
                    });
        }
        // 从 mybatis-plus 3.5 开始
        if(mybatisAutoConfiguration==null){
            mybatisAutoConfiguration = Reflects.newInstance(MybatisPlusAutoConfiguration.class,
                    new Class[]{
                            MybatisPlusProperties.class,
                            ObjectProvider.class,
                            ObjectProvider.class,
                            ObjectProvider.class,
                            ResourceLoader.class,
                            ObjectProvider.class,
                            ObjectProvider.class,
                            ObjectProvider.class,
                            ObjectProvider.class,
                            ApplicationContext.class
                    },
                    new Object[]{
                            properties,
                            interceptorsProvider,
                            typeHandlerProvider,
                            languageDriverProvider,
                            resourceLoader,
                            databaseIdProviderObjectProvider,
                            configurationCustomizersProvider,
                            sqlSessionFactoryBeanCustomizers,
                            mybatisPlusPropertiesCustomizerProvider,
                            this.applicationContext
                    });
        }

        Preconditions.checkNotNull(mybatisAutoConfiguration, "the mybatis-plus 3.x autoconfiguration is null");
        mybatisAutoConfiguration.afterPropertiesSet();
        try {
            return mybatisAutoConfiguration.sqlSessionFactory(dataSource);
        }catch (Throwable e){
            logger.error(e.getMessage(), e);
            throw Throwables.wrapAsRuntimeException(e);
        }
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(
            MybatisPlusProperties mybatisProperties,
            SqlSessionFactory sessionFactory,
            MethodInvocationDataSourceKeySelector selector) {
        DynamicSqlSessionTemplate template = new DynamicSqlSessionTemplate(sessionFactory, mybatisProperties.getExecutorType());
        template.setSelector(selector);
        return template;
    }
}
