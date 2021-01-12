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
import com.jn.langx.invocation.aop.expression.AspectJExpressionPointcutAdvisorProperties;
import com.jn.sqlhelper.datasource.config.DynamicDataSourcesProperties;
import com.jn.sqlhelper.datasource.key.MethodDataSourceKeyRegistry;
import com.jn.sqlhelper.datasource.spring.aop.DataSourceKeyChoicesAnnotationMethodInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @since 3.4.0
 */
@Configuration
@ConditionalOnProperty(name = "sqlhelper.dynamic-datasource.enabled", havingValue = "true")
@ConditionalOnClass(AspectJExpressionPointcutAdvisorProperties.class)
public class DynamicMethodDataSourcesKeyAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(DynamicMethodDataSourcesKeyAutoConfiguration.class);


    @Bean("annotationKeyChoicesAdvisor")
    @ConditionalOnProperty(prefix = "sqlhelper.dynamic-datasource.keyChoices", name = "expression")
    @ConditionalOnMissingBean(name = "annotationKeyChoicesAdvisor")
    public AspectJExpressionPointcutAdvisor keyChoicesAdvisor(
            DynamicDataSourcesProperties namedDataSourcesProperties,
            MethodDataSourceKeyRegistry keyRegistry) {

        DataSourceKeyChoicesAnnotationMethodInterceptor interceptor = new DataSourceKeyChoicesAnnotationMethodInterceptor();
        interceptor.setKeyRegistry(keyRegistry);

        AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisorBuilder()
                .properties(namedDataSourcesProperties.getKeyChoices())
                .interceptor(interceptor)
                .build();
        logger.info("===[SQLHelper & Dynamic DataSource]=== Add datasource key choices interceptor, use an expression: {}", namedDataSourcesProperties.getKeyChoices().getExpression());
        return advisor;
    }
}
