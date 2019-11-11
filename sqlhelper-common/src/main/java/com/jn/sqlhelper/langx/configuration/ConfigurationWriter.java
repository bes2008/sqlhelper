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

public interface ConfigurationWriter<T extends Configuration> {

    /**
     * whether supports write or not
     *
     * @return true if supports, else false
     */
    boolean isSupportsWrite();

    /**
     * add a configuration
     *
     * @param configuration the configuration
     */
    void write(T configuration);

    /**
     * whether supports rewrite or not
     *
     * @return true if supports, else false
     */
    boolean isSupportsRewrite();

    /**
     * update a configuration
     *
     * @param configuration the configuration
     */
    void rewrite(T configuration);

    /**
     * whether supports remove or not
     *
     * @return true if supports, else false
     */
    boolean isSupportsRemove();

    /**
     * remove a configuration by id
     *
     * @param id the configuration id
     */
    void remove(String id);

}
