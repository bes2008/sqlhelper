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

import com.jn.langx.cache.AbstractCacheLoader;
import com.jn.sqlhelper.langx.configuration.Configuration;
import com.jn.sqlhelper.langx.configuration.ConfigurationLoader;

public class DirectoryBasedFileConfigurationCacheLoaderAdapter<T extends Configuration> extends AbstractCacheLoader<String, T> {
    private ConfigurationLoader<T> configurationLoader;

    public DirectoryBasedFileConfigurationCacheLoaderAdapter(ConfigurationLoader<T> configurationLoader) {
        this.configurationLoader = configurationLoader;
    }

    @Override
    public T load(String key) {
        return configurationLoader.load(key);
    }
}
