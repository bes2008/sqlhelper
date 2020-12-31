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

import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.multivalue.CommonMultiValueMap;
import com.jn.langx.util.collection.multivalue.MultiValueMap;
import com.jn.langx.util.function.Consumer2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class DataSourcesProperties {
    private static final Logger logger = LoggerFactory.getLogger(DataSourcesProperties.class);
    private boolean enabled;

    private List<DataSourceProperties> dataSources = Collects.emptyArrayList();
    private String defaultRouter;
    /**
     * 配置每个group 的 slaves 节点采用的router算法
     * key: group name
     * value: routers
     */
    private MultiValueMap<String, String> groupRouters;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<DataSourceProperties> getDataSources() {
        return dataSources;
    }

    public void setDataSources(List<DataSourceProperties> dataSources) {
        this.dataSources = dataSources;
    }

    public String getDefaultRouter() {
        return defaultRouter;
    }

    public void setDefaultRouter(String defaultRouter) {
        this.defaultRouter = defaultRouter;
    }

    public void setGroupRouters(Map<String, String> mapping) {
        this.groupRouters = new CommonMultiValueMap<String, String>();
        Collects.forEach(mapping, new Consumer2<String, String>() {
            @Override
            public void accept(String group, String routerString) {
                if (Strings.isBlank(routerString)) {
                    logger.warn("the routers string is blank for the group: {}", group);
                } else {
                    groupRouters.addAll(group, Collects.asList(Strings.split(routerString, ",")));
                }
            }
        });
    }

    public MultiValueMap<String, String> getGroupRouters() {
        return groupRouters;
    }
}
