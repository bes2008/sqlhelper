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

package com.jn.sqlhelper.common.utils;

import com.jn.langx.text.StrTokenizer;
import com.jn.langx.util.Strings;
import com.jn.langx.util.regexp.Regexp;
import com.jn.langx.util.regexp.Regexps;
import com.jn.sqlhelper.common.ddl.model.DatabaseDescription;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Locale;
import java.util.StringTokenizer;

public class SQLs {
    public static final String WHITESPACE = Strings.WHITESPACE;
    public static final String SQL_FILE_SUFFIX = ".sql";

    public static String getTableFQN(String catalog, String schema, String tableName) {
        return getTableFQN(catalog, schema, tableName, true);
    }

    public static String getTableFQN(String catalog, String schema, String tableName, boolean catalogAtStart) {
        return getTableFQN(catalog, schema, tableName, null, catalogAtStart);
    }

    public static String getTableFQN(String catalog, String schema, String tableName, String separator, boolean catalogAtStart) {
        if (Strings.isEmpty(separator)) {
            separator = ".";
        }
        String fqn = tableName;
        if (catalogAtStart) {
            if (Strings.isNotEmpty(schema)) {
                fqn = schema + separator + fqn;
            }
            if (Strings.isNotEmpty(catalog)) {
                fqn = catalog + separator + fqn;
            }
        } else {
            if (Strings.isNotEmpty(schema)) {
                fqn = fqn + separator + schema;
            }
            if (Strings.isNotEmpty(catalog)) {
                fqn = fqn + separator + catalog;
            }
        }
        return fqn;
    }

    public static String getTableFQN(DatabaseMetaData metaData, String catalog, String schema, String tableName) {
        return getTableFQN(new DatabaseDescription(metaData), catalog, schema, tableName);
    }

    public static String getTableFQN(DatabaseDescription databaseDesc, String catalog, String schema, String tableName) {
        String catalogSeparator = databaseDesc.getCatalogSeparator();
        return SQLs.getTableFQN(catalog, schema, tableName, catalogSeparator, databaseDesc.isCatalogAtStart());
    }

    public static int findPlaceholderParameterCount(String sqlsegment){
        if(Strings.isNotEmpty(sqlsegment)) {
            sqlsegment = sqlsegment.replaceAll("([\\\\][?])", "");
            sqlsegment = sqlsegment.replaceAll("[^?]", "");
            sqlsegment = sqlsegment.replaceAll("'\\?'", "");
            return sqlsegment.length();
        }
        return 0;
    }

    //DML
    public static boolean isSelectStatement(String sql) {
        String sql0 = sql.trim();
        // with xx as ( select x ...
        if (Strings.startsWithIgnoreCase(sql0, "with")) {
            StrTokenizer stringTokenizer = new StrTokenizer(sql0);
            int i = 0;
            while (i < 5 && stringTokenizer.hasNext()) {
                String token = stringTokenizer.next();
                if ("select".equals(token.toLowerCase())) {
                    return true;
                }
                i++;
            }
            return false;
        } else {
            return Strings.startsWithIgnoreCase(sql0, "select");
        }
    }

    private static final Regexp SELECT_COUNT_PATTERN = Regexps.createRegexp("select\\s+count.*");
    private static final Regexp COUNT_FUNCTION_PATTERN = Regexps.createRegexp("count(\\s*\\(.*(\\s*\\))?)?");

    public static boolean isSelectCountStatement(String sql) {
        String sql0 = sql.trim();
        // with xx as ( select x ...
        if (Strings.startsWithIgnoreCase(sql0, "with")) {
            StrTokenizer stringTokenizer = new StrTokenizer(sql0);
            int i = 0;
            boolean hasSelectKeyword = false;
            boolean hasCountKeyword = false;

            while (i < 7 && stringTokenizer.hasNext()) {
                String token = stringTokenizer.next();
                if ("select".equals(token.toLowerCase())) {
                    hasSelectKeyword = true;
                    continue;
                }
                if (COUNT_FUNCTION_PATTERN.matcher(token.toLowerCase()).matches()) {
                    hasCountKeyword = true;
                }
                i++;
            }
            return hasSelectKeyword && hasCountKeyword;
        } else {
            String lowerSql = sql0.toLowerCase();
            return SELECT_COUNT_PATTERN.matcher(lowerSql).matches();
        }
    }

    public static boolean isUpdateStatement(String sql) {
        sql = sql.trim();
        return sql.toLowerCase(Locale.ROOT).replaceAll("\\s", " ").matches("update \\w+(\\.\\w)* set");
    }

    public static boolean isDeleteStatement(String sql) {
        sql = sql.trim();
        return sql.toLowerCase(Locale.ROOT).replaceAll("\\s", " ").startsWith("delete from");
    }

    public static boolean isInsertStatement(String sql) {
        sql = sql.trim();
        return sql.toLowerCase(Locale.ROOT).replaceAll("\\s", " ").startsWith("insert into");
    }


    // DDL
    public static boolean isCreateStatement(String sql) {
        sql = sql.trim();
        return sql.toLowerCase(Locale.ROOT).replaceAll("\\s", " ").startsWith("create table");
    }

    public static boolean isDropTableStatement(String sql) {
        sql = sql.trim();
        return sql.toLowerCase(Locale.ROOT).replaceAll("\\s", " ").startsWith("drop table");
    }

    public static boolean isQuote(String tok) {
        return "\"".equals(tok) ||
                "`".equals(tok) ||
                "]".equals(tok) ||
                "[".equals(tok) ||
                "'".equals(tok);
    }

    public static boolean isSupportsBatchUpdates(Connection conn) {
        try {
            return new DatabaseDescription(conn.getMetaData()).supportsBatchUpdates();
        } catch (SQLException ex) {
            return false;
        }
    }

}
