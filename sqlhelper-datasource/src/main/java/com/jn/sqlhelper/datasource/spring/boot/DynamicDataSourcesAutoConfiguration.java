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
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Function;
import com.jn.sqlhelper.datasource.DataSourceRegistry;
import com.jn.sqlhelper.datasource.DataSources;
import com.jn.sqlhelper.datasource.NamedDataSource;
import com.jn.sqlhelper.datasource.definition.DataSourceProperties;
import com.jn.sqlhelper.datasource.definition.NamedDataSourcesProperties;
import com.jn.sqlhelper.datasource.factory.CentralizedDataSourceFactory;
import com.jn.sqlhelper.datasource.key.DataSourceKey;
import com.jn.sqlhelper.datasource.key.DataSourceKeyRegistry;
import com.jn.sqlhelper.datasource.key.DataSourceKeySelector;
import com.jn.sqlhelper.datasource.key.parser.DataSourceKeyAnnotationParser;
import com.jn.sqlhelper.datasource.key.parser.DataSourceKeyDataSourceParser;
import com.jn.sqlhelper.datasource.key.router.DataSourceKeyRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.ListFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    public NamedDataSourcesProperties namedDataSourcesProperties(Environment environment) {
        String keyChoicesPointcutExpression = environment.getProperty("sqlhelper.dynamicDataSource.key-choices-pointcut.expression");
        if (Strings.isNotBlank(keyChoicesPointcutExpression)) {
            String requiredClass = "com.jn.agileway.spring.aop.AspectJExpressionPointcutAdvisorProperties";
            if (!ClassLoaders.hasClass(requiredClass, this.getClass().getClassLoader())) {
                logger.warn("The configuration property 'sqlhelper.dynamicDataSource.key-choices-pointcut.expression' has specified, but can't find the class: '{}', you should import com.github.fangjinuo.agilway:agileway-spring:${VERSION}.jar to you classpath", requiredClass);
            }
        }
        return new NamedDataSourcesProperties();
    }

    @Bean(name = "dataSourcesFactoryBean")
    public ListFactoryBean dataSourcesFactoryBean(
            final CentralizedDataSourceFactory centralizedDataSourceFactory,
            NamedDataSourcesProperties namedDataSourcesProperties,
            // 该参数只是为了兼容Spring Boot 默认的 DataSource配置而已
            ObjectProvider<DataSource> springBootOriginDataSourceProvider) {
        logger.info("===[SQLHelper & Dynamic DataSource]=== the dynamic datasource is enabled");

        List<DataSourceProperties> dataSourcePropertiesList = namedDataSourcesProperties.getDataSources();
        List<NamedDataSource> dataSources = Pipeline.of(dataSourcePropertiesList).map(new Function<DataSourceProperties, NamedDataSource>() {
            @Override
            public NamedDataSource apply(DataSourceProperties dataSourceProperties) {
                return centralizedDataSourceFactory.get(dataSourceProperties);
            }
        }).clearNulls().asList();

        // 处理 Spring Boot 默认数据源
        DataSource springBootOriginDataSource = springBootOriginDataSourceProvider.getIfAvailable();
        if (springBootOriginDataSource != null) {
            NamedDataSource namedDataSource = DataSources.toNamedDataSource(springBootOriginDataSource);
            if (dataSources.isEmpty()) {
                namedDataSource.setName(DataSources.DATASOURCE_PRIMARY);
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
    public DataSourceKeyRegistry dataSourceKeyRegistry(ObjectProvider<List<DataSourceKeyAnnotationParser>> dataSourceKeyAnnotationParsersProvider) {
        final DataSourceKeyRegistry registry = new DataSourceKeyRegistry();
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
            DataSourceRegistry registry,
            DataSourceKeyRegistry keyRegistry,
            ObjectProvider<List<DataSourceKeyRouter>> routersProvider) {
        DataSourceKeySelector selector = new DataSourceKeySelector();
        selector.setDataSourceRegistry(registry);
        List<DataSourceKeyRouter> routers = routersProvider.getIfAvailable();
        selector.addDataSourceKeyRouters(routers);
        selector.setDataSourceKeyRegistry(keyRegistry);
        return selector;
    }


}
