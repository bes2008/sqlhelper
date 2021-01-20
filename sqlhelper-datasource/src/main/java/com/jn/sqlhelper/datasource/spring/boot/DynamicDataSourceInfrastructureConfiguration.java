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

package com.jn.sqlhelper.datasource.spring.boot;

import com.jn.langx.util.ClassLoaders;
import com.jn.langx.util.Strings;
import com.jn.sqlhelper.datasource.DataSourceRegistry;
import com.jn.sqlhelper.datasource.config.DataSourcePropertiesCipherer;
import com.jn.sqlhelper.datasource.config.DataSourcePropertiesRsaCipherer;
import com.jn.sqlhelper.datasource.config.DynamicDataSourcesProperties;
import com.jn.sqlhelper.datasource.factory.CentralizedDataSourceFactory;
import com.jn.sqlhelper.datasource.key.parser.DataSourceKeyDataSourceParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @since 3.4.5
 */
@Configuration
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
public class DynamicDataSourceInfrastructureConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceInfrastructureConfiguration.class);


    /**
     * @since 3.4.0
     */
    @Bean
    public DataSourceRegistry dataSourceRegistry(ObjectProvider<DataSourceKeyDataSourceParser> dataSourceKeyParserProvider) {
        DataSourceRegistry dataSourceRegistry = new DataSourceRegistry();
        DataSourceKeyDataSourceParser dataSourceKeyParser = dataSourceKeyParserProvider.getIfAvailable();
        dataSourceRegistry.setKeyParser(dataSourceKeyParser);
        return dataSourceRegistry;
    }

    /**
     * @since 3.4.0
     */
    @Bean
    public CentralizedDataSourceFactory centralizedDataSourceFactory(DataSourceRegistry dataSourceRegistry) {
        CentralizedDataSourceFactory factory = new CentralizedDataSourceFactory();
        factory.setRegistry(dataSourceRegistry);
        return factory;
    }

    /**
     * @since 3.4.0
     */
    @Bean
    @ConfigurationProperties(prefix = "sqlhelper.dynamic-datasource")
    public DynamicDataSourcesProperties namedDataSourcesProperties(Environment environment) {
        String keyChoicesPointcutExpression = environment.getProperty("sqlhelper.dynamic-datasource.key-choices.expression");
        if (Strings.isNotBlank(keyChoicesPointcutExpression)) {
            String requiredClass = "com.jn.agileway.spring.aop.AspectJExpressionPointcutAdvisorBuilder";
            if (!ClassLoaders.hasClass(requiredClass, this.getClass().getClassLoader())) {
                StringBuilder log = new StringBuilder("The configuration property 'sqlhelper.dynamicDataSource.key-choices.expression' has specified, but can't find the class: '" + requiredClass + "', you should import the following jars to your classpath:\n")
                        .append("\t1) com.github.fangjinuo.agilway:agileway-spring:${agileway.version}.jar\n")
                        .append("\t2) org.springframework:spring-aop:${spring.version}.jar\n")
                        .append("\t3) org.aspectj:aspectjweaver:${aspectj.version}.jar\n")
                        .append("\t4) com.github.fangjinuo.langx:langx-java:${langx-java.version}.jar\n");
                logger.warn(log.toString());
            }
        }
        return new DynamicDataSourcesProperties();
    }

    /**
     * @since 3.4.5
     */
    @Bean
    @ConditionalOnMissingBean
    public DataSourcePropertiesCipherer dataSourcePropertiesCipherer(ObjectProvider<DynamicDataSourcesProperties> dynamicDataSourcesPropertiesProvider) {
        DataSourcePropertiesRsaCipherer cipherer = new DataSourcePropertiesRsaCipherer();
        DynamicDataSourcesProperties dynamicDataSourcesProperties = dynamicDataSourcesPropertiesProvider.getIfAvailable();
        if (dynamicDataSourcesProperties != null) {
            if (Strings.isNotBlank(dynamicDataSourcesProperties.getPublicKey())) {
                cipherer.setPublicKey(dynamicDataSourcesProperties.getPublicKey());
            }
            if (Strings.isNotBlank(dynamicDataSourcesProperties.getPrivateKey())) {
                cipherer.setPrivateKey(dynamicDataSourcesProperties.getPrivateKey());
            }
        }
        return cipherer;
    }


}
