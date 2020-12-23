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
import com.jn.sqlhelper.datasource.key.DataSourceKeyRegistry;
import com.jn.sqlhelper.datasource.spring.aop.DataSourceKeyChoicesAnnotationMethodInterceptor;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(name = "sqlhelper.dynamicDataSource.enabled", havingValue = "true", matchIfMissing = false)
@Configuration
@ImportAutoConfiguration(DynamicDataSourcesAutoConfiguration.class)
public class DynamicDataSourcesKeyChoicesAutoConfiguration {
    @Bean("aspectJExpressionPointcutAdvisorProperties")
    @ConditionalOnProperty(prefix = "sqlhelper.dynamicDataSource", name = "key-choices-pointcut")
    public AspectJExpressionPointcutAdvisorProperties aspectJExpressionPointcutAdvisorProperties() {
        return new AspectJExpressionPointcutAdvisorProperties();
    }

    @Bean
    @ConditionalOnProperty(prefix = "sqlhelper.dynamicDataSource", name = "key-choices-pointcut")
    public DataSourceKeyChoicesAnnotationMethodInterceptor interceptor(DataSourceKeyRegistry keyRegistry) {
        DataSourceKeyChoicesAnnotationMethodInterceptor interceptor = new DataSourceKeyChoicesAnnotationMethodInterceptor();
        interceptor.setKeyRegistry(keyRegistry);
        return interceptor;
    }

    @Bean("annotationKeyChoicesAdvisor")
    @ConditionalOnProperty(prefix = "sqlhelper.dynamicDataSource", name = "key-choices-pointcut")
    @ConditionalOnMissingBean(name = "annotationKeyChoicesAdvisor")
    public AspectJExpressionPointcutAdvisor keyChoicesAdvisor(
            AspectJExpressionPointcutAdvisorProperties properties,
            DataSourceKeyChoicesAnnotationMethodInterceptor interceptor) {
        return new AspectJExpressionPointcutAdvisorBuilder()
                .properties(properties)
                .interceptor(interceptor)
                .build();
    }
}
