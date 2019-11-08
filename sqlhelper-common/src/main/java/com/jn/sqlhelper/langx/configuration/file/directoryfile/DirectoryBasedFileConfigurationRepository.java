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

import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.diff.MapDiffResult;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.io.file.FileFilter;
import com.jn.sqlhelper.langx.configuration.AbstractConfigurationRepository;
import com.jn.sqlhelper.langx.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * multiple configuration file in one directory, every configuration will be load as a configuration
 *
 * @param <T>
 */
public class DirectoryBasedFileConfigurationRepository<T extends Configuration> extends AbstractConfigurationRepository<T, DirectoryBasedFileConfigurationLoader<T>, DirectoryBasedFileConfigurationWriter<T>> {
    private static final Logger logger = LoggerFactory.getLogger(DirectoryBasedFileConfigurationRepository.class);
    /**
     * configuration file filter
     */
    private List<FileFilter> fileFilters;

    /**
     * units: seconds
     * scan interval, if <=0, will not refresh
     */
    private int refreshIntervalInSeconds = 60;
    private long nextRefreshTime = System.currentTimeMillis();

    private String directory;


    private Map<String, Long> lastModifiedTimeMap = Collects.emptyHashMap();

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public void setRefreshIntervalInSeconds(int refreshIntervalInSeconds) {
        this.refreshIntervalInSeconds = refreshIntervalInSeconds;
    }

    @Override
    public void init() throws InitializationException {
        super.init();
        loader.setDirectory(directory);
    }

    private void computeNextRefreshTime() {
        nextRefreshTime = System.currentTimeMillis() + refreshIntervalInSeconds * 1000;
    }

    @Override
    public void startup() {
        super.startup();
    }

    private void reload() {
        Map<String, Long> modifiedTimeMap = loader.scanConfigurationFileModifiedTimes();
        try {
            MapDiffResult<String, Long> lastModifiedDiffResult = Collects.diff(lastModifiedTimeMap, modifiedTimeMap);
            Collects.forEach(lastModifiedDiffResult.getRemoves(), new Consumer2<String, Long>() {
                @Override
                public void accept(String id, Long lastModified) {
                    T configuration = removeById(id);
                    if (Emptys.isNotNull(configuration)) {
                        // TODO publish event
                    }
                }
            });
            Collects.forEach(lastModifiedDiffResult.getUpdates(), new Consumer2<String, Long>() {
                @Override
                public void accept(String id, Long lastModified) {
                    T configuration = loader.load(id);
                    update(configuration);
                    // TODO publish event
                }
            });
            Collects.forEach(lastModifiedDiffResult.getAdds(), new Consumer2<String, Long>() {
                @Override
                public void accept(String id, Long lastModified) {
                    T configuration = loader.load(id);
                    add(configuration);
                    // TODO publish event
                }
            });
        } finally {
            lastModifiedTimeMap = modifiedTimeMap;
        }
    }

}
