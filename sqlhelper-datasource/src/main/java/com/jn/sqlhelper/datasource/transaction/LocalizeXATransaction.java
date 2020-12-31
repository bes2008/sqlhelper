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

package com.jn.sqlhelper.datasource.transaction;

import com.jn.langx.util.collection.buffer.ArrayBuffer;
import com.jn.sqlhelper.datasource.key.DataSourceKey;

import java.sql.Connection;

class LocalizeXATransaction {
    private ArrayBuffer connections = new ArrayBuffer(1024);

    public void addBranch(DataSourceKey key, Connection conn) {
        //connections. new Entry<DataSourceKey, Connection>(key, conn);
    }
}
