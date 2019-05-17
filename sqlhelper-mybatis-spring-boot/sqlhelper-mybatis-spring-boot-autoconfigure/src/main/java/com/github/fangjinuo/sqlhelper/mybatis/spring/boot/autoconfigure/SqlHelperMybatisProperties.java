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

package com.github.fangjinuo.sqlhelper.mybatis.spring.boot.autoconfigure;

import com.github.fangjinuo.sqlhelper.dialect.conf.SQLInstrumentConfig;
import com.github.fangjinuo.sqlhelper.mybatis.plugins.pagination.PaginationPluginConfig;
import com.github.fangjinuo.sqlhelper.dialect.conf.SQLInstrumentConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "sqlhelper.mybatis")
public class SqlHelperMybatisProperties {

    @NestedConfigurationProperty
    private SQLInstrumentConfig instrumentor = new SQLInstrumentConfig();
    @NestedConfigurationProperty
    private PaginationPluginConfig pagination = new PaginationPluginConfig();

    public SQLInstrumentConfig getInstrumentor() {
        return instrumentor;
    }

    public void setInstrumentor(SQLInstrumentConfig instrumentor) {
        this.instrumentor = instrumentor;
    }

    public PaginationPluginConfig getPagination() {
        return pagination;
    }

    public void setPagination(PaginationPluginConfig pagination) {
        this.pagination = pagination;
    }
}
