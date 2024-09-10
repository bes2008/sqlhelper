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

package com.jn.sqlhelper.mybatis;

import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.sqlhelper.dialect.DialectRegistry;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Properties;
import java.util.Set;

public class CustomVendorDatabaseIdProvider extends VendorDatabaseIdProvider implements DatabaseIdProvider {
    private static final Logger logger = LoggerFactory.getLogger(CustomVendorDatabaseIdProvider.class);

    public CustomVendorDatabaseIdProvider() {
    }

    @Override
    public String getDatabaseId(DataSource dataSource) {
        if (dataSource == null) {
            throw new NullPointerException("dataSource cannot be null");
        }
        try {
            return DialectRegistry.guessDatabaseId(dataSource);
        } catch (Exception e) {
            logger.error("Could not get a databaseId from dataSource", e);
        }
        return null;
    }

    @Override
    public void setProperties(final Properties p) {
        Set<String> keys = p.stringPropertyNames();
        Collects.forEach(keys, new Consumer<String>() {
            @Override
            public void accept(String key) {
                String databaseId = p.getProperty(key);
                if (Strings.isNotBlank(databaseId)) {
                    databaseId = Strings.trim(databaseId);
                    DialectRegistry.setDatabaseNameIfAbsent(key, databaseId);
                }
            }
        });
    }
}
