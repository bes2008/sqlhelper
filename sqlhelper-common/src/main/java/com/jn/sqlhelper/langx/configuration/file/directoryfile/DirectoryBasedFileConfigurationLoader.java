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

package com.jn.sqlhelper.langx.configuration.file.directoryfile;

import com.jn.langx.io.resource.FileResource;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Supplier;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.io.file.FileFilter;
import com.jn.langx.util.io.file.Filenames;
import com.jn.langx.util.io.file.filter.IsFileFilter;
import com.jn.langx.util.io.file.filter.ReadableFileFilter;
import com.jn.sqlhelper.langx.configuration.Configuration;
import com.jn.sqlhelper.langx.configuration.ConfigurationLoader;
import com.jn.sqlhelper.langx.configuration.InputStreamConfigurationParser;
import com.jn.sqlhelper.langx.io.file.AllFileFilter;
import com.jn.sqlhelper.langx.io.resource.DirectoryBasedFileResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class DirectoryBasedFileConfigurationLoader<T extends Configuration> implements ConfigurationLoader<T> {
    private static final Logger logger = LoggerFactory.getLogger(DirectoryBasedFileConfigurationLoader.class);
    private DirectoryBasedFileResourceLoader resourceLoader;
    private Supplier<String, String> filenameSupplier = new Supplier<String, String>() {
        @Override
        public String get(String input) {
            return input;
        }
    };
    private InputStreamConfigurationParser<T> configurationParser;
    private List<? extends FileFilter> filters = Collects.asList(new ReadableFileFilter(), new IsFileFilter());
    private Supplier<String, String> configurationIdSupplier = new Supplier<String, String>() {
        @Override
        public String get(String input) {
            return input;
        }
    };

    public void setDirectory(String directory) {
        resourceLoader = new DirectoryBasedFileResourceLoader(directory);
    }

    public void setFilenameSupplier(Supplier<String, String> filenameSupplier) {
        this.filenameSupplier = filenameSupplier;
    }

    public void setConfigurationParser(InputStreamConfigurationParser<T> configurationParser) {
        this.configurationParser = configurationParser;
    }

    @Override
    public T load(String id) {
        FileResource fileResource = resourceLoader.loadResource(filenameSupplier.get(id));
        InputStream inputStream = null;
        try {
            inputStream = fileResource.getInputStream();
            T configuration = configurationParser.parse(inputStream);
            configuration.setId(id);
        } catch (Throwable ex) {
            logger.info("Error occur when load configuration: {}", id);
        } finally {
            IOs.close(inputStream);
        }
        return null;
    }

    public Map<String, Long> scanConfigurationFileModifiedTimes() {
        return Pipeline.of(resourceLoader.listFiles())
                .filter(new AllFileFilter(this.filters))
                .collect(Collects.toHashMap(
                        new Function<File, String>() {
                            @Override
                            public String apply(File file) {
                                return configurationIdSupplier.get(Filenames.extractFilename(file.getAbsolutePath(), false));
                            }
                        },
                        new Function<File, Long>() {
                            @Override
                            public Long apply(File file) {
                                return file.lastModified();
                            }
                        },

                        false));
    }
}
