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
import com.jn.langx.util.timing.timer.HashedWheelTimer;
import com.jn.sqlhelper.common.connection.NamedConnectionConfiguration;
import com.jn.sqlhelper.common.connection.PropertiesNamedConnectionConfigurationParser;
import com.jn.sqlhelper.common.connection.PropertiesNamedConnectionConfigurationSerializer;
import com.jn.sqlhelper.langx.configuration.file.directoryfile.DirectoryBasedFileConfigurationCacheLoaderAdapter;
import com.jn.sqlhelper.langx.configuration.file.directoryfile.DirectoryBasedFileConfigurationLoader;
import com.jn.sqlhelper.langx.configuration.file.directoryfile.DirectoryBasedFileConfigurationRepository;
import com.jn.sqlhelper.langx.configuration.file.directoryfile.DirectoryBasedFileConfigurationWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JdbcConnectionRepositoryProperties.class)
public class JdbcConnectionRepositoryConfig {

    @Autowired
    private JdbcConnectionRepositoryProperties jdbcProperties;

    @Bean
    public PropertiesNamedConnectionConfigurationParser propertiesNamedConnectionConfigurationParser() {
        return new PropertiesNamedConnectionConfigurationParser();
    }

    @Bean("jdbcDirectoryBasedFileConfigurationLoader")
    public DirectoryBasedFileConfigurationLoader<NamedConnectionConfiguration> jdbcDirectoryBasedFileConfigurationLoader(@Autowired PropertiesNamedConnectionConfigurationParser propertiesConfigurationParser) {
        DirectoryBasedFileConfigurationLoader<NamedConnectionConfiguration> loader = new DirectoryBasedFileConfigurationLoader<NamedConnectionConfiguration>();
        loader.setConfigurationParser(propertiesConfigurationParser);
        return loader;
    }

    @Bean
    public Cache<String, NamedConnectionConfiguration> jdbcConnectionConfigurationCache(DirectoryBasedFileConfigurationLoader<NamedConnectionConfiguration> loader) {
        return CacheBuilder.<String, NamedConnectionConfiguration>newBuilder().loader(new DirectoryBasedFileConfigurationCacheLoaderAdapter<NamedConnectionConfiguration>(loader)).build();
    }


    @Bean
    public PropertiesNamedConnectionConfigurationSerializer propertiesNamedConnectionConfigurationSerializer() {
        return new PropertiesNamedConnectionConfigurationSerializer();
    }

    @Bean("jdbcDirectoryBasedFileConfigurationWriter")
    public DirectoryBasedFileConfigurationWriter<NamedConnectionConfiguration> directoryBasedFileConfigurationWriter(@Autowired PropertiesNamedConnectionConfigurationSerializer serializer) {
        DirectoryBasedFileConfigurationWriter<NamedConnectionConfiguration> writer = new DirectoryBasedFileConfigurationWriter<NamedConnectionConfiguration>();
        writer.setConfigurationSerializer(serializer);
        writer.setEncoding("iso-8859-1");
        return writer;
    }

    @Bean
    public DirectoryBasedFileConfigurationRepository<NamedConnectionConfiguration> directoryPropertiesFileConfigurationRepository(
            @Autowired @Qualifier("jdbcConnectionConfigurationCache") Cache<String, NamedConnectionConfiguration> cache,
            @Autowired @Qualifier("jdbcDirectoryBasedFileConfigurationLoader") DirectoryBasedFileConfigurationLoader<NamedConnectionConfiguration> loader,
            @Autowired @Qualifier("jdbcDirectoryBasedFileConfigurationWriter") DirectoryBasedFileConfigurationWriter<NamedConnectionConfiguration> writer,
            @Autowired HashedWheelTimer timer) {
        DirectoryBasedFileConfigurationRepository<NamedConnectionConfiguration> repository = new DirectoryBasedFileConfigurationRepository<NamedConnectionConfiguration>();
        repository.setCache(cache);
        repository.setName("JdbcConnectionConfigurationRepository");
        repository.setDirectory(jdbcProperties.getDirectory());
        repository.setConfigurationLoader(loader);
        repository.setConfigurationWriter(writer);
        repository.setTimer(timer);
        repository.setReloadIntervalInSeconds(jdbcProperties.getReloadIntervalInSeconds());
        repository.startup();
        return repository;
    }

}
