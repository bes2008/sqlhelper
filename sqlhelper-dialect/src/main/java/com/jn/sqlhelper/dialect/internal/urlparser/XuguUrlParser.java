/*
 * Copyright 2021 the original author or authors.
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

package com.jn.sqlhelper.dialect.internal.urlparser;

// com.xugu.cloudjdbc.Driver
// 虚谷数据库 http://www.xugucn.com/Single_index_id_30.shtml


import com.jn.langx.util.collection.Collects;

import java.util.List;

/**
 * jdbc:xugu://serverIP:portNumber/databaseName[?property=value[&property=value]]
 */
public class XuguUrlParser extends CommonUrlParser {
    private static final String URL_PREFIX = "jdbc:xugu:";
    private static final List<String> URL_SCHEMAS = Collects.newArrayList(URL_PREFIX);

    @Override
    public List<String> getUrlSchemas() {
        return URL_SCHEMAS;
    }
}
