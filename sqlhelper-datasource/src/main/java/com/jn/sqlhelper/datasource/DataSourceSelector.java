/*
 * Copyright 2020 the original author or authors.
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

package com.jn.sqlhelper.datasource;

import com.jn.sqlhelper.datasource.key.DataSourceKey;

public abstract class DataSourceSelector {
    protected DataSourceRegistry registry;

    public void setDataSourceRegistry(DataSourceRegistry registry) {
        this.registry = registry;
    }

    /**
     * 指定的group下，选择某个datasource, 返回的是
     */
    public abstract DataSourceKey select();
}
