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

import com.jn.agileway.spring.aop.AspectJExpressionPointcutAdvisorBuilder;
import com.jn.langx.invocation.aop.expression.AspectJExpressionPointcutAdvisorProperties;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.logging.Level;
import com.jn.langx.util.logging.Loggers;
import com.jn.sqlhelper.common.transaction.DefaultTransactionManager;
import com.jn.sqlhelper.common.transaction.TransactionDefinitionRegistry;
import com.jn.sqlhelper.common.transaction.TransactionManager;
import com.jn.sqlhelper.common.transaction.definition.parser.NamedTransactionDefinitionParser;
import com.jn.sqlhelper.common.transaction.definition.parser.TransactionDefinitionParser;
import com.jn.sqlhelper.datasource.config.DynamicDataSourcesProperties;
import com.jn.sqlhelper.datasource.spring.aop.LocalizeGlobalTransactionInterceptor;
import com.jn.sqlhelper.datasource.spring.transaction.definition.EmptyTransactionAttributeSource;
import com.jn.sqlhelper.datasource.spring.transaction.definition.SpringTransactionAttributeSourceAdapter;
import com.jn.sqlhelper.datasource.spring.transaction.definition.SpringTransactionalAnnotationParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ListFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.util.List;

@Configuration
@AutoConfigureAfter(DynamicDataSourcesAutoConfiguration.class)
@ConditionalOnProperty(name = "sqlhelper.dynamicDataSource.enabled", havingValue = "true")
@ConditionalOnClass(AspectJExpressionPointcutAdvisorProperties.class)
public class DynamicTransactionAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourcesAutoConfiguration.class);

    @Bean
    @ConditionalOnProperty(prefix = "sqlhelper.dynamicDataSource.transaction", name = "expression")
    public TransactionManager dynamicTransactionManager() {
        DefaultTransactionManager transactionManager = new DefaultTransactionManager();
        Loggers.log(3, logger, Level.WARN, null, "the sqlhelper dynamic datasource transaction manager is enabled with the configuration: sqlhelper.dynamicDataSource.transaction, so you should make sure the spring transaction manager is not enabled");
        return transactionManager;
    }


    @Bean
    @ConditionalOnProperty(prefix = "sqlhelper.dynamicDataSource.transaction", name = "expression")
    public SpringTransactionalAnnotationParser springTransactionalAnnotationParser() {
        return new SpringTransactionalAnnotationParser();
    }


    @Bean
    @ConditionalOnProperty(prefix = "sqlhelper.dynamicDataSource.transaction", name = "expression")
    public TransactionDefinitionRegistry transactionDefinitionRegistry(ObjectProvider<List<TransactionDefinitionParser>> parserProvider,
                                                                       /**
                                                                        * 该变量在方法内部不会用到，但是不能删除，为了保证注入顺序
                                                                        */
                                                                       @Qualifier("dataSourcesFactoryBean")
                                                                       ListFactoryBean dataSourcesFactoryBean,
                                                                       ObjectProvider<List<TransactionInterceptor>> springTransactionInterceptorProvider) {
        final TransactionDefinitionRegistry registry = new TransactionDefinitionRegistry();
        List<TransactionDefinitionParser> parsers = parserProvider.getIfAvailable();
        Collects.forEach(parsers, new Consumer<TransactionDefinitionParser>() {
            @Override
            public void accept(TransactionDefinitionParser parser) {
                registry.register(parser);
            }
        });

        List<TransactionInterceptor> springTransactionInterceptors = springTransactionInterceptorProvider.getIfAvailable();
        if (Emptys.isNotEmpty(springTransactionInterceptors)) {
            final TransactionAttributeSource replacement = new EmptyTransactionAttributeSource();
            Collects.forEach(springTransactionInterceptors, new Consumer<TransactionInterceptor>() {
                @Override
                public void accept(TransactionInterceptor springTransactionInterceptor) {
                    TransactionAttributeSource attributeSource = springTransactionInterceptor.getTransactionAttributeSource();
                    if (attributeSource != null) {
                        // 禁用 Spring 的事务功能
                        springTransactionInterceptor.setTransactionAttributeSource(replacement);
                        // 接管Spring的事务配置
                        NamedTransactionDefinitionParser parser = new SpringTransactionAttributeSourceAdapter(attributeSource);
                        registry.register(parser);
                    }
                }
            });
        }

        return registry;
    }


    @Bean("dynamicDataSourceTransactionAdvisor")
    @ConditionalOnProperty(prefix = "sqlhelper.dynamicDataSource.transaction", name = "expression")
    @ConditionalOnMissingBean(name = "dynamicDataSourceTransactionAdvisor")
    public AspectJExpressionPointcutAdvisor dynamicDataSourceTransactionAdvisor(
            DynamicDataSourcesProperties properties,
            TransactionManager transactionManager,
            TransactionDefinitionRegistry registry) {

        LocalizeGlobalTransactionInterceptor interceptor = new LocalizeGlobalTransactionInterceptor();
        interceptor.setTransactionManager(transactionManager);
        interceptor.setDefinitionRegistry(registry);

        AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisorBuilder()
                .properties(properties.getTransaction())
                .interceptor(interceptor)
                .build();
        logger.info("===[SQLHelper & Dynamic DataSource]=== Add dynamic datasource transaction interceptor, use an expression: {}", properties.getTransaction().getExpression());
        return advisor;
    }
}
