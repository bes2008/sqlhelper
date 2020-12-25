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

import com.jn.agileway.spring.aop.AspectJExpressionPointcutAdvisorBuilder;
import com.jn.agileway.spring.aop.AspectJExpressionPointcutAdvisorProperties;
import com.jn.sqlhelper.datasource.key.MethodDataSourceKeyRegistry;
import com.jn.sqlhelper.datasource.spring.aop.DataSourceKeyChoicesAnnotationMethodInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "sqlhelper.dynamicDataSource.enabled", havingValue = "true")
@ConditionalOnClass(AspectJExpressionPointcutAdvisorProperties.class)
public class DynamicDataSourcesKeyChoicesAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourcesKeyChoicesAutoConfiguration.class);

    @Bean("aspectJExpressionPointcutAdvisorProperties")
    @ConfigurationProperties(prefix = "sqlhelper.dynamicDataSource.key-choices-pointcut")
    public AspectJExpressionPointcutAdvisorProperties aspectJExpressionPointcutAdvisorProperties() {
        return new AspectJExpressionPointcutAdvisorProperties();
    }

    @Bean
    @ConditionalOnProperty(prefix = "sqlhelper.dynamicDataSource.key-choices-pointcut", name = "expression")
    public DataSourceKeyChoicesAnnotationMethodInterceptor interceptor(MethodDataSourceKeyRegistry keyRegistry) {
        DataSourceKeyChoicesAnnotationMethodInterceptor interceptor = new DataSourceKeyChoicesAnnotationMethodInterceptor();
        interceptor.setKeyRegistry(keyRegistry);
        return interceptor;
    }

    @Bean("annotationKeyChoicesAdvisor")
    @ConditionalOnProperty(prefix = "sqlhelper.dynamicDataSource.key-choices-pointcut", name = "expression")
    @ConditionalOnMissingBean(name = "annotationKeyChoicesAdvisor")
    public AspectJExpressionPointcutAdvisor keyChoicesAdvisor(
            AspectJExpressionPointcutAdvisorProperties properties,
            DataSourceKeyChoicesAnnotationMethodInterceptor interceptor) {
        AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisorBuilder()
                .properties(properties)
                .interceptor(interceptor)
                .build();
        logger.info("===[SQLHelper]=== Add datasource key choices interceptor, use an expression: {}", properties.getExpression());
        return advisor;
    }
}
