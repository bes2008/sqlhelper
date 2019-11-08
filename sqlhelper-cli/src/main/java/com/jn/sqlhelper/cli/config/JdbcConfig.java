/*
 * Copyright 2019 the original author or authors.
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

package com.jn.sqlhelper.cli.config;

import com.jn.langx.cache.Cache;
import com.jn.langx.cache.CacheBuilder;
import com.jn.sqlhelper.common.connection.DirectoryPropertiesFileConfigurationRepository;
import com.jn.sqlhelper.common.connection.NamedConnectionConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JdbcConfig {
    @Bean
    public Cache<String, NamedConnectionConfiguration> jdbcConnectionConfigurationCache(){
        return CacheBuilder.<String, NamedConnectionConfiguration>newBuilder().build();
    }

    @Bean
    public DirectoryPropertiesFileConfigurationRepository directoryPropertiesFileConfigurationRepository(@Qualifier("jdbcConnectionConfigurationCache") Cache<String, NamedConnectionConfiguration> cache){
        DirectoryPropertiesFileConfigurationRepository repository = new DirectoryPropertiesFileConfigurationRepository();
        repository.setCache(cache);
        return repository;
    }

}
