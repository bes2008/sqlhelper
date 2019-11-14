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
import com.jn.langx.configuration.ConfigurationEventFactory;
import com.jn.langx.configuration.file.directoryfile.DirectoryBasedFileConfigurationCacheLoaderAdapter;
import com.jn.langx.configuration.file.directoryfile.DirectoryBasedFileConfigurationLoader;
import com.jn.langx.configuration.file.directoryfile.DirectoryBasedFileConfigurationRepository;
import com.jn.langx.configuration.file.directoryfile.DirectoryBasedFileConfigurationWriter;
import com.jn.langx.event.EventPublisher;
import com.jn.langx.event.local.SimpleEventPublisher;
import com.jn.langx.util.Strings;
import com.jn.langx.util.function.Supplier;
import com.jn.langx.util.io.file.FileFilter;
import com.jn.langx.util.io.file.filter.PatternFilenameFilter;
import com.jn.langx.util.timing.timer.Timer;
import com.jn.sqlhelper.common.connection.NamedConnectionConfiguration;
import com.jn.sqlhelper.common.connection.PropertiesNamedConnectionConfigurationParser;
import com.jn.sqlhelper.common.connection.PropertiesNamedConnectionConfigurationSerializer;
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

    @Bean("configurationIdSupplier")
    public Supplier<String, String> configurationIdSupplier() {
        return new Supplier<String, String>() {
            @Override
            public String get(String filename) {
                return Strings.replace(filename, "jdbcConn-", "");
            }
        };
    }

    @Bean
    public Supplier<String, String> filenameSupplier() {
        return new Supplier<String, String>() {
            @Override
            public String get(String configurationId) {
                return "jdbcConn-" + configurationId + ".properties";
            }
        };
    }

    @Bean
    public FileFilter jdbcConnectionConfigPatternFilter() {
        return new PatternFilenameFilter("jdbcConn-.*\\.properties");
    }


    @Bean("jdbcDirectoryBasedFileConfigurationLoader")
    public DirectoryBasedFileConfigurationLoader<NamedConnectionConfiguration> jdbcDirectoryBasedFileConfigurationLoader(
            @Autowired PropertiesNamedConnectionConfigurationParser propertiesConfigurationParser,
            @Autowired @Qualifier("filenameSupplier") Supplier<String, String> filenameSupplier,
            @Autowired @Qualifier("configurationIdSupplier") Supplier<String, String> configurationIdSupplier) {
        DirectoryBasedFileConfigurationLoader<NamedConnectionConfiguration> loader = new DirectoryBasedFileConfigurationLoader<NamedConnectionConfiguration>();
        loader.setFilenameSupplier(filenameSupplier);
        loader.setConfigurationIdSupplier(configurationIdSupplier);
        loader.setConfigurationParser(propertiesConfigurationParser);
        return loader;
    }

    @Bean
    public Cache<String, NamedConnectionConfiguration> jdbcConnectionConfigurationCache(
            @Autowired @Qualifier("jdbcDirectoryBasedFileConfigurationLoader")
                    DirectoryBasedFileConfigurationLoader<NamedConnectionConfiguration> loader,
            @Autowired @Qualifier("timer")
                    Timer timer) {
        return CacheBuilder.<String, NamedConnectionConfiguration>newBuilder()
                .loader(new DirectoryBasedFileConfigurationCacheLoaderAdapter<NamedConnectionConfiguration>(loader))
                .timer(timer)
                .build();
    }


    @Bean
    public PropertiesNamedConnectionConfigurationSerializer propertiesNamedConnectionConfigurationSerializer() {
        return new PropertiesNamedConnectionConfigurationSerializer();
    }

    @Bean("jdbcDirectoryBasedFileConfigurationWriter")
    public DirectoryBasedFileConfigurationWriter<NamedConnectionConfiguration> directoryBasedFileConfigurationWriter(
            @Autowired PropertiesNamedConnectionConfigurationSerializer serializer,
            @Autowired @Qualifier("filenameSupplier") Supplier<String, String> filenameSupplier) {
        DirectoryBasedFileConfigurationWriter<NamedConnectionConfiguration> writer = new DirectoryBasedFileConfigurationWriter<NamedConnectionConfiguration>();
        writer.setFilenameSupplier(filenameSupplier);
        writer.setConfigurationSerializer(serializer);
        writer.setEncoding("iso-8859-1");
        return writer;
    }

    @Bean("jdbcConfigurationEventFactory")
    public ConfigurationEventFactory<NamedConnectionConfiguration> configurationEventFactory() {
        return new ConfigurationEventFactory<NamedConnectionConfiguration>("JdbcConnectionConfiguration");
    }

    @Bean
    public EventPublisher eventPublisher() {
        EventPublisher eventPublisher = new SimpleEventPublisher();
        eventPublisher.addEventListener("JdbcConnectionConfiguration", new LogConfigurationMutationEventListener());
        return eventPublisher;
    }

    @Bean
    public DirectoryBasedFileConfigurationRepository<NamedConnectionConfiguration> directoryPropertiesFileConfigurationRepository(
            @Autowired @Qualifier("jdbcConnectionConfigurationCache") Cache<String, NamedConnectionConfiguration> cache,
            @Autowired @Qualifier("jdbcDirectoryBasedFileConfigurationLoader") DirectoryBasedFileConfigurationLoader<NamedConnectionConfiguration> loader,
            @Autowired @Qualifier("jdbcDirectoryBasedFileConfigurationWriter") DirectoryBasedFileConfigurationWriter<NamedConnectionConfiguration> writer,

            @Autowired @Qualifier("jdbcConfigurationEventFactory") ConfigurationEventFactory<NamedConnectionConfiguration> eventFactory,
            @Autowired @Qualifier("jdbcConnectionConfigPatternFilter") FileFilter jdbcConnectionConfigPatternFilter,
            @Autowired EventPublisher eventPublisher,
            @Autowired Timer timer) {
        DirectoryBasedFileConfigurationRepository<NamedConnectionConfiguration> repository = new DirectoryBasedFileConfigurationRepository<NamedConnectionConfiguration>();
        repository.setCache(cache);
        repository.setName("JdbcConnectionConfigurationRepository");
        repository.setDirectory(jdbcProperties.getDirectory());
        repository.setConfigurationLoader(loader);
        repository.setConfigurationWriter(writer);
        repository.setTimer(timer);
        repository.setEventPublisher(eventPublisher);
        repository.setEventFactory(eventFactory);
        repository.setReloadIntervalInSeconds(jdbcProperties.getReloadIntervalInSeconds());
        repository.startup();
        return repository;
    }

}
