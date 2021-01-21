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


import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
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

    @Bean(name = "sqlSessionFactory")
    public DynamicSqlSessionFactory dynamicSqlSessionFactory(
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
                        SqlSessionFactory delegate = createSqlSessionFactory(dataSource, properties, interceptorsProvider, typeHandlerProvider, languageDriverProvider, resourceLoader, databaseIdProvider, configurationCustomizersProvider, mybatisPlusPropertiesCustomizerProvider);
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

        Preconditions.checkNotNull(mybatisAutoConfiguration, "the mybatis-plus 3.x autoconfiguration is null");
        mybatisAutoConfiguration.afterPropertiesSet();
        return mybatisAutoConfiguration.sqlSessionFactory(dataSource);
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
