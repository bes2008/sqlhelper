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
     * Load bean definitions from the specified resources.
     *
     * @param resources the resource descriptors
     * @return the number of bean definitions found
     */
    List<DataSourceProperties> loadBeanDefinitions(Resource... resources);


}
