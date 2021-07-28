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

package com.jn.sqlhelper.examples.mybatis.config;

import com.jn.sqlhelper.examples.mybatis.plugins.Interceptor0;
import com.jn.sqlhelper.examples.mybatis.plugins.Interceptor1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class MybatisInterceptorConfig {
    @Bean
    @Order(Integer.MAX_VALUE)
    public Interceptor0 interceptor0(){
        return new Interceptor0();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public Interceptor1 interceptor1(){
        return new Interceptor1();
    }
}
