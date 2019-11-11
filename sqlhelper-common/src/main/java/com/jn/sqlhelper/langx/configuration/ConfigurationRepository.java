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

package com.jn.sqlhelper.langx.configuration;

import com.jn.langx.event.EventPublisherAware;
import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.Lifecycle;

public interface ConfigurationRepository<T extends Configuration, Loader extends ConfigurationLoader<T>, Writer extends ConfigurationWriter<T>> extends Lifecycle, EventPublisherAware, Initializable {

    /**
     * set a loader
     *
     * @param loader the configuration loader
     */
    void setConfigurationLoader(Loader loader);

    /**
     * set a writer
     *
     * @param writer the configuration writer
     */
    void setConfigurationWriter(Writer writer);

    /**
     * Get a configuration bean by a specified id
     *
     * @param id the id
     * @return a configuration
     */
    T getById(String id);

    /**
     * Remove a configuration by id
     * it equals: <code>removeById(T configuration, true)</code>
     *
     * @param id the configuration id
     * @return the removed configuration
     */
    void removeById(String id);

    /**
     * Remove a configuration by id
     *
     * @param id   the configuration id
     * @param sync whether sync to the real storage or not
     * @return the removed configuration
     */
    void removeById(String id, boolean sync);

    /**
     * Add a configuration to the repository
     * it equals: <code>add(T configuration, true)</code>
     *
     * @param configuration the configuration bean
     * @return true if add success
     */
    void add(T configuration);

    /**
     * @param configuration the configuration
     * @param sync          whether sync to the real storage or not
     * @return true if add success, else false
     */
    void add(T configuration, boolean sync);

    /**
     * update the configuration to storage
     * it equals: <code>update(T configuration, true)</code>
     *
     * @param configuration the configuration bean
     */
    void update(T configuration);

    /**
     * update the configuration to storage
     *
     * @param configuration the configuration bean
     * @param sync          whether sync to the real storage or not
     */
    void update(T configuration, boolean sync);

}
