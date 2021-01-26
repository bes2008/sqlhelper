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

package com.jn.sqlhelper.mybatis.springboot.autoconfigure;

import com.jn.sqlhelper.datasource.key.MethodInvocationDataSourceKeySelector;
import com.jn.sqlhelper.mybatis.session.factory.SimpleSqlSessionFactoryProvider;
import com.jn.sqlhelper.mybatis.session.factory.SqlSessionFactoryProvider;
import com.jn.sqlhelper.mybatis.spring.session.factory.dynamicdatasource.DynamicSqlSessionFactory;
import com.jn.sqlhelper.mybatis.spring.session.factory.dynamicdatasource.DynamicSqlSessionFactoryMethodInvocationProvider;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SqlSessionFactoryProviderAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SqlSessionFactoryProvider sqlSessionFactoryProvider(
            ObjectProvider<MethodInvocationDataSourceKeySelector> selectorObjectProvider,
            SqlSessionFactory sessionFactory) {
        if (sessionFactory instanceof DynamicSqlSessionFactory) {
            MethodInvocationDataSourceKeySelector selector = selectorObjectProvider.getObject();
            return new DynamicSqlSessionFactoryMethodInvocationProvider((DynamicSqlSessionFactory) sessionFactory, selector);
        }
        return new SimpleSqlSessionFactoryProvider(sessionFactory);
    }
}
