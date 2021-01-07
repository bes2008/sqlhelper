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

package com.jn.sqlhelper.datasource.config;

/**
 * 数据源组的配置
 *
 * @since 3.4.1
 */
public class DataSourceGroupProperties {
    /**
     * 数据源组名称
     */
    private String name;
    /**
     * @see com.jn.sqlhelper.datasource.key.WriteOperationMethodMatcher
     * 提供哪些方法是要进行写的，将会自动的视为使用master datasource
     */
    private String writePattern;
    /**
     * @see com.jn.sqlhelper.datasource.key.router.DataSourceKeyRouter
     * 组内 slave 节点的 负责均衡算法
     */
    private String router;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWritePattern() {
        return writePattern;
    }

    public void setWritePattern(String writePattern) {
        this.writePattern = writePattern;
    }

    public String getRouter() {
        return router;
    }

    public void setRouter(String router) {
        this.router = router;
    }
}
