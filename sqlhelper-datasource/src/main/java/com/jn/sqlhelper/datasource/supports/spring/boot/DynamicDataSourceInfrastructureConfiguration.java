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

package com.jn.sqlhelper.datasource.supports.spring.boot;

import com.jn.langx.util.ClassLoaders;
import com.jn.langx.util.Strings;
import com.jn.sqlhelper.common.security.DriverPropertiesCipher;
import com.jn.sqlhelper.common.security.DriverPropertiesRsaCipher;
import com.jn.sqlhelper.datasource.DataSourceRegistry;
import com.jn.sqlhelper.datasource.DataSources;
import com.jn.sqlhelper.datasource.config.DataSourceProperties;
import com.jn.sqlhelper.datasource.config.DynamicDataSourcesProperties;
import com.jn.sqlhelper.datasource.factory.CentralizedDataSourceFactory;
import com.jn.sqlhelper.datasource.key.parser.DataSourceKeyDataSourceParser;
import com.jn.sqlhelper.datasource.supports.spring.config.DynamicDataSourcesPropertiesFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * @since 3.4.5
 */
@Configuration
@EnableConfigurationProperties(org.springframework.boot.autoconfigure.jdbc.DataSourceProperties.class)
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
public class DynamicDataSourceInfrastructureConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceInfrastructureConfiguration.class);

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
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "sqlhelper.dynamic-datasource")
    public DynamicDataSourcesProperties namedDataSourcesProperties(
            ObjectProvider<DynamicDataSourcesPropertiesFactoryBean> dataSourcesPropertiesFactoryBeanProvider,
            Environment environment) {

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

        DynamicDataSourcesPropertiesFactoryBean factoryBean = dataSourcesPropertiesFactoryBeanProvider.getIfAvailable();
        if (factoryBean != null) {
            try {
                return factoryBean.getObject();
            } catch (Throwable ex) {
                logger.error(ex.getMessage(), ex);
            }
        }

        return new DynamicDataSourcesProperties();
    }


    /**
     * @since 3.4.0
     */
    @Bean
    public DataSourceRegistry dataSourceRegistry(ObjectProvider<DataSourceKeyDataSourceParser> dataSourceKeyParserProvider, DynamicDataSourcesProperties dynamicDataSourcesProperties) {
        DataSourceRegistry dataSourceRegistry = new DataSourceRegistry();
        DataSourceKeyDataSourceParser dataSourceKeyParser = dataSourceKeyParserProvider.getIfAvailable();
        dataSourceRegistry.setKeyParser(dataSourceKeyParser);
        dataSourceRegistry.setHealthCheckTimeout(dynamicDataSourcesProperties.getHealthCheckTimeout());
        dataSourceRegistry.init();
        return dataSourceRegistry;
    }


    /**
     * @since 3.4.5
     */
    @Bean
    @ConditionalOnMissingBean
    public DriverPropertiesCipher dataSourcePropertiesCipherer(
            ObjectProvider<DynamicDataSourcesProperties> dynamicDataSourcesPropertiesObjectProvider,
            // 由于 Spring 的构建顺序的原因，这里不能去直接使用 Spring Boot 里的DataSourceProperties, 用了也没有意义
            ObjectProvider<org.springframework.boot.autoconfigure.jdbc.DataSourceProperties> dataSourcePropertiesObjectProvider) {

        DriverPropertiesRsaCipher cipherer = new DriverPropertiesRsaCipher();


        DynamicDataSourcesProperties dynamicDataSourcesProperties = dynamicDataSourcesPropertiesObjectProvider.getIfAvailable();
        if (dynamicDataSourcesProperties != null) {
            if (Strings.isNotBlank(dynamicDataSourcesProperties.getPublicKey())) {
                cipherer.setPublicKey(dynamicDataSourcesProperties.getPublicKey());
            }
            if (Strings.isNotBlank(dynamicDataSourcesProperties.getPrivateKey())) {
                cipherer.setPrivateKey(dynamicDataSourcesProperties.getPrivateKey());
            }

            // 直接就开始进行 解密操作
            List<DataSourceProperties> dataSourcePropertiesList = dynamicDataSourcesProperties.getDatasources();
            if (dataSourcePropertiesList != null && !dataSourcePropertiesList.isEmpty()) {
                for (DataSourceProperties dataSourceProperties : dataSourcePropertiesList) {
                    String username = dataSourceProperties.getUsername();
                    if (Strings.isNotBlank(username)) {
                        username = DataSources.decrypt(cipherer, username);
                        dataSourceProperties.setUsername(username);
                    }
                    String password = dataSourceProperties.getPassword();
                    if (Strings.isNotBlank(password)) {
                        password = DataSources.decrypt(cipherer, password);
                        dataSourceProperties.setPassword(password);
                    }
                }
            }

        }

        // 对Spring内置属性进行解密操作
        org.springframework.boot.autoconfigure.jdbc.DataSourceProperties springBuiltinDataSourceProperties = dataSourcePropertiesObjectProvider.getIfAvailable();
        if (springBuiltinDataSourceProperties != null) {
            String username = springBuiltinDataSourceProperties.getUsername();
            if (Strings.isNotBlank(username)) {
                username = DataSources.decrypt(cipherer, username);
                springBuiltinDataSourceProperties.setUsername(username);
            }
            String password = springBuiltinDataSourceProperties.getPassword();
            if (Strings.isNotBlank(password)) {
                password = DataSources.decrypt(cipherer, password);
                springBuiltinDataSourceProperties.setPassword(password);
            }
        }

        return cipherer;
    }


}
