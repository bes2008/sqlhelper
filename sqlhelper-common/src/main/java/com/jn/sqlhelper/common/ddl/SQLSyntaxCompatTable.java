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

package com.jn.sqlhelper.common.ddl;

import com.jn.langx.annotation.Singleton;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Supplier;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 很多数据库的语法都是差不多一样的，有的是基于一下知名数据库改造的，有的是开发时就考虑到兼容某些数据库的语法的
 */
@Singleton
public class SQLSyntaxCompatTable {

    public static final String MYSQL = "mysql";
    public static final String ORACLE = "oracle";
    public static final String SQLSERVER = "sqlserver";
    public static final String POSTGRESQL = "postgresql";

    private static final SQLSyntaxCompatTable INSTANCE = new SQLSyntaxCompatTable();

    private final Map<String, Set<String>> mapping = Collects.emptyNonAbsentHashMap(new Supplier<String, Set<String>>() {
        @Override
        public Set<String> get(String s) {
            Set<String> set = new LinkedHashSet<String>();
            set.add(s);
            return set;
        }
    });

    private SQLSyntaxCompatTable() {
    }

    public static SQLSyntaxCompatTable getInstance() {
        return INSTANCE;
    }

    public void register(String databaseId, String[] compatDatabaseIds) {
        mapping.get(databaseId).addAll(Collects.asList(compatDatabaseIds));
    }

    public void register(String databaseId, String compatDatabaseId) {
        mapping.get(databaseId).add(compatDatabaseId);
    }

    public Set<String> getCompatDatabases(String databaseId) {
        return mapping.get(databaseId);
    }

    /**
     *
     * @param databaseId your test database id
     * @param compatDatabaseId for examples : mysql
     */
    public boolean isCompatible(String databaseId, String compatDatabaseId) {
        return getCompatDatabases(databaseId).contains(compatDatabaseId);
    }
}
