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

package com.jn.sqlhelper.datasource.spring.boot;

import com.jn.langx.util.ClassLoaders;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Predicate;
import com.jn.sqlhelper.datasource.DataSourceRegistry;
import com.jn.sqlhelper.datasource.DataSources;
import com.jn.sqlhelper.datasource.NamedDataSource;
import com.jn.sqlhelper.datasource.definition.DataSourceProperties;
import com.jn.sqlhelper.datasource.definition.DataSourcesProperties;
import com.jn.sqlhelper.datasource.factory.CentralizedDataSourceFactory;
import com.jn.sqlhelper.datasource.key.DataSourceKey;
import com.jn.sqlhelper.datasource.key.DataSourceKeySelector;
import com.jn.sqlhelper.datasource.key.MethodDataSourceKeyRegistry;
import com.jn.sqlhelper.datasource.key.WriteOperationMethodMatcher;
import com.jn.sqlhelper.datasource.key.parser.DataSourceKeyAnnotationParser;
import com.jn.sqlhelper.datasource.key.parser.DataSourceKeyDataSourceParser;
import com.jn.sqlhelper.datasource.key.router.DataSourceKeyRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.ListFactoryBean;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@ConditionalOnProperty(name = "sqlhelper.dynamicDataSource.enabled", havingValue = "true", matchIfMissing = false)
@Configuration
public class DynamicDataSourcesAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourcesAutoConfiguration.class);


    @Bean
    public DataSourceRegistry dataSourceRegistry(ObjectProvider<DataSourceKeyDataSourceParser> dataSourceKeyParserProvider) {
        DataSourceRegistry dataSourceRegistry = new DataSourceRegistry();
        DataSourceKeyDataSourceParser dataSourceKeyParser = dataSourceKeyParserProvider.getIfAvailable();
        dataSourceRegistry.setKeyParser(dataSourceKeyParser);
        return dataSourceRegistry;
    }

    @Bean
    public CentralizedDataSourceFactory centralizedDataSourceFactory(DataSourceRegistry dataSourceRegistry) {
        CentralizedDataSourceFactory factory = new CentralizedDataSourceFactory();
        factory.setRegistry(dataSourceRegistry);
        return factory;
    }

    @Bean
    @ConfigurationProperties(prefix = "sqlhelper.dynamicDataSource")
    public DataSourcesProperties namedDataSourcesProperties(Environment environment) {
        String keyChoicesPointcutExpression = environment.getProperty("sqlhelper.dynamicDataSource.key-choices-pointcut.expression");
        if (Strings.isNotBlank(keyChoicesPointcutExpression)) {
            String requiredClass = "com.jn.agileway.spring.aop.AspectJExpressionPointcutAdvisorProperties";
            if (!ClassLoaders.hasClass(requiredClass, this.getClass().getClassLoader())) {
                StringBuilder log = new StringBuilder("The configuration property 'sqlhelper.dynamicDataSource.key-choices-pointcut.expression' has specified, but can't find the class: '" + "com.jn.agileway.spring.aop.AspectJExpressionPointcutAdvisorProperties" + "', you should import the following jars to your classpath:\n")
                        .append("\t1) com.github.fangjinuo.agilway:agileway-spring:${agileway.version}.jar\n")
                        .append("\t2) org.springframework:spring-aop:${spring.version}.jar\n")
                        .append("\t3) org.aspectj:aspectjweaver:${aspectj.version}.jar\n");
                logger.warn(log.toString());
            }
        }
        return new DataSourcesProperties();
    }

    // 把数据源放到Spring容器中
    //public GenericBeanDefinition


    @Bean(name = "dataSourcesFactoryBean")
    public ListFactoryBean dataSourcesFactoryBean(
            final CentralizedDataSourceFactory centralizedDataSourceFactory,
            DataSourcesProperties namedDataSourcesProperties,
            // 该参数只是为了兼容Spring Boot 默认的 DataSource配置而已
            ObjectProvider<DataSource> springBootOriginDataSourceProvider,
            ObjectProvider<org.springframework.boot.autoconfigure.jdbc.DataSourceProperties> builtInDataSourceProperties,
            final ApplicationContext applicationContext) {

        logger.info("===[SQLHelper & Dynamic DataSource]=== the dynamic datasource is enabled");

        final List<NamedDataSource> dataSources = Collects.emptyArrayList();
        // 处理 Spring Boot 默认数据源
        DataSource springBootOriginDataSource = springBootOriginDataSourceProvider.getIfAvailable();
        boolean isTestDB = true;
        if (springBootOriginDataSource != null) {
            org.springframework.boot.autoconfigure.jdbc.DataSourceProperties properties = builtInDataSourceProperties.getObject();
            if (Objs.equals(properties.determineUrl(), properties.getUrl())) {
                isTestDB = false;
            }
        }

        List<DataSourceProperties> dataSourcePropertiesList = namedDataSourcesProperties.getDataSources();

        // spring bean factory
        final AbstractAutowireCapableBeanFactory beanFactory = ((AbstractAutowireCapableBeanFactory) applicationContext.getAutowireCapableBeanFactory());
        Pipeline.of(dataSourcePropertiesList).forEach(new Consumer<DataSourceProperties>() {
            @Override
            public void accept(DataSourceProperties dataSourceProperties) {
                NamedDataSource namedDataSource = centralizedDataSourceFactory.get(dataSourceProperties);
                if (namedDataSource != null) {
                    // 注册 DataSource对象到 Spring 容器中
                    String beanName = namedDataSource.getDataSourceKey().getId();
                    beanFactory.registerSingleton(beanName, namedDataSource);
                    logger.info("===[SQLHelper & Dynamic DataSource]=== register jdbc datasource bean {} to spring bean factory", beanName);
                    dataSources.add(namedDataSource);
                }
            }
        });

        if (!isTestDB) {
            DataSourceProperties dataSourceProperties = SpringDataSourcePropertiesAdapter.adapt(builtInDataSourceProperties.getObject());
            NamedDataSource namedDataSource = DataSources.toNamedDataSource(springBootOriginDataSource, dataSourceProperties.getName(), dataSourceProperties);
            if (dataSources.isEmpty()) {
                namedDataSource.setName(DataSources.DATASOURCE_PRIMARY_NAME);
            }
            centralizedDataSourceFactory.getRegistry().register(namedDataSource);
            dataSources.add(namedDataSource);
        }

        if (logger.isInfoEnabled() && !dataSources.isEmpty()) {
            StringBuilder log = new StringBuilder(256);
            log.append("===[SQLHelper & Dynamic DataSource]=== will load dataSources:\n\t");
            Collection<DataSourceKey> keys = Collects.map(dataSources, new Function<NamedDataSource, DataSourceKey>() {
                @Override
                public DataSourceKey apply(NamedDataSource namedDataSource) {
                    return namedDataSource.getDataSourceKey();
                }
            });
            log.append(Strings.join("\n\t", keys));
            logger.info(log.toString());
        }

        ListFactoryBean dataSourcesFactoryBean = new ListFactoryBean();
        dataSourcesFactoryBean.setTargetListClass(ArrayList.class);
        dataSourcesFactoryBean.setSourceList(dataSources);
        return dataSourcesFactoryBean;
    }

    @Bean
    public MethodDataSourceKeyRegistry dataSourceKeyRegistry(ObjectProvider<List<DataSourceKeyAnnotationParser>> dataSourceKeyAnnotationParsersProvider) {
        final MethodDataSourceKeyRegistry registry = new MethodDataSourceKeyRegistry();
        List<DataSourceKeyAnnotationParser> parsers = dataSourceKeyAnnotationParsersProvider.getIfAvailable();
        Collects.forEach(parsers, new Consumer<DataSourceKeyAnnotationParser>() {
            @Override
            public void accept(DataSourceKeyAnnotationParser dataSourceKeyAnnotationParser) {
                registry.registerDataSourceKeyParser(dataSourceKeyAnnotationParser);
            }
        });
        return registry;
    }

    @Bean
    public DataSourceKeySelector dataSourceKeySelector(
            final DataSourceRegistry registry,
            MethodDataSourceKeyRegistry keyRegistry,
            ObjectProvider<List<DataSourceKeyRouter>> routersProvider,
            DataSourcesProperties dataSourcesProperties) {

        final DataSourceKeySelector selector = new DataSourceKeySelector();

        selector.setDataSourceKeyRegistry(keyRegistry);
        selector.setDataSourceRegistry(registry);

        List<DataSourceKeyRouter> routers = routersProvider.getIfAvailable();
        selector.registerRouters(routers);

        // 处理 default router
        final String defaultRouterName = dataSourcesProperties.getDefaultRouter();
        if (Emptys.isNotEmpty(defaultRouterName)) {
            DataSourceKeyRouter defaultRouter = Collects.findFirst(routers, new Predicate<DataSourceKeyRouter>() {
                @Override
                public boolean test(DataSourceKeyRouter dataSourceKeyRouter) {
                    return defaultRouterName.equals(dataSourceKeyRouter.getName());
                }
            });
            if (defaultRouter != null) {
                selector.setDefaultRouter(defaultRouter);
            }
        }

        // 指定分配关系
        Map<String, String> groupToRoutersMap = dataSourcesProperties.getGroupRouters();
        Collects.forEach(groupToRoutersMap, new Consumer2<String, String>() {
            @Override
            public void accept(String group, String router) {
                selector.allocateRouter(group, router);
            }
        });

        final Map<String, String> groupToWritePattern = dataSourcesProperties.getGroupWriterPatternMap();
        Collects.forEach(groupToWritePattern, new Consumer2<String, String>() {
            @Override
            public void accept(String group, String writePattern) {
                WriteOperationMethodMatcher writeOperationMethodMatcher = new WriteOperationMethodMatcher(writePattern);
                selector.allocateWriteMatcher(group, writeOperationMethodMatcher);
            }
        });

        selector.init();

        return selector;
    }

}
