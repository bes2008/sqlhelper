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
import com.jn.sqlhelper.common.connection.NamedConnectionConfiguration;
import com.jn.sqlhelper.common.connection.PropertiesNamedConnectionConfigurationParser;
import com.jn.sqlhelper.langx.configuration.file.directoryfile.DirectoryBasedFileConfigurationLoader;
import com.jn.sqlhelper.langx.configuration.file.directoryfile.DirectoryBasedFileConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JdbcProperties.class)
public class JdbcConfig {

    @Autowired
    private JdbcProperties jdbcProperties;

    @Bean
    public Cache<String, NamedConnectionConfiguration> jdbcConnectionConfigurationCache() {
        return CacheBuilder.<String, NamedConnectionConfiguration>newBuilder().build();
    }

    @Bean
    public PropertiesNamedConnectionConfigurationParser propertiesConfigurationParser() {
        return new PropertiesNamedConnectionConfigurationParser();
    }

    @Bean("jdbcDirectoryBasedFileConfigurationLoader")
    public DirectoryBasedFileConfigurationLoader<NamedConnectionConfiguration> jdbcDirectoryBasedFileConfigurationLoader(@Autowired PropertiesNamedConnectionConfigurationParser propertiesConfigurationParser) {
        DirectoryBasedFileConfigurationLoader<NamedConnectionConfiguration> loader = new DirectoryBasedFileConfigurationLoader<NamedConnectionConfiguration>();
        loader.setConfigurationParser(propertiesConfigurationParser);
        return loader;
    }

    @Bean
    public DirectoryBasedFileConfigurationRepository<NamedConnectionConfiguration> directoryPropertiesFileConfigurationRepository(
            @Autowired @Qualifier("jdbcConnectionConfigurationCache") Cache<String, NamedConnectionConfiguration> cache,
            @Autowired @Qualifier("jdbcDirectoryBasedFileConfigurationLoader") DirectoryBasedFileConfigurationLoader<NamedConnectionConfiguration> loader) {
        DirectoryBasedFileConfigurationRepository<NamedConnectionConfiguration> repository = new DirectoryBasedFileConfigurationRepository<NamedConnectionConfiguration>();
        repository.setCache(cache);
        repository.setName("JdbcConnectionConfigurationRepository");
        repository.setDirectory(jdbcProperties.getDirectory());
        repository.setConfigurationLoader(loader);
        repository.init();
        repository.startup();
        return repository;
    }

}
