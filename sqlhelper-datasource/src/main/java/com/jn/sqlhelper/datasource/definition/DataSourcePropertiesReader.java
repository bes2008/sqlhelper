/*
 * Copyright 2020 the original author or authors.
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

package com.jn.sqlhelper.datasource.definition;


import com.jn.langx.io.resource.Resource;

import java.util.List;

public interface DataSourcePropertiesReader {

    /**
     * Load bean definitions from the specified resource.
     *
     * @param resource the resource descriptor
     * @return the number of bean definitions found
     */
    List<DataSourceProperties> loadBeanDefinitions(Resource resource);

    /**
     * Load bean definitions from the specified resources.
     *
     * @param resources the resource descriptors
     * @return the number of bean definitions found
     */
    List<DataSourceProperties> loadBeanDefinitions(Resource... resources);

    /**
     * Load bean definitions from the specified resource location.
     * <p>The location can also be a location pattern, provided that the
     * ResourceLoader of this bean definition reader is a ResourcePatternResolver.
     *
     * @param location the resource location, to be loaded with the ResourceLoader
     *                 (or ResourcePatternResolver) of this bean definition reader
     * @return the number of bean definitions found
     */
    List<DataSourceProperties> loadBeanDefinitions(String location);

    /**
     * Load bean definitions from the specified resource locations.
     *
     * @param locations the resource locations, to be loaded with the ResourceLoader
     *                  (or ResourcePatternResolver) of this bean definition reader
     * @return the number of bean definitions found
     */
    List<DataSourceProperties> loadBeanDefinitions(String... locations);

}
