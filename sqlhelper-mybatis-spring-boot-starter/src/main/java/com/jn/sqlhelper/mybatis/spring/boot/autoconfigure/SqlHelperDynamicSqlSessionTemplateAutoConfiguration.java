/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the LGPL, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jn.sqlhelper.mybatis.spring.boot.autoconfigure;

import com.jn.sqlhelper.mybatis.spring.DynamicSqlSessionTemplate;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SqlHelperDynamicSqlSessionTemplateAutoConfiguration {
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(MybatisProperties mybatisProperties, SqlSessionFactory sessionFactory) {
        DynamicSqlSessionTemplate template = new DynamicSqlSessionTemplate(sessionFactory, mybatisProperties.getExecutorType());
        return template;
    }
}
