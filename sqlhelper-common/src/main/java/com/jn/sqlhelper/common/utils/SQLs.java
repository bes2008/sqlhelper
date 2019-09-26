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

import com.jn.langx.util.Strings;

import java.util.Locale;
import java.util.StringTokenizer;

public class SQLs {
    public static final String WHITESPACE = " \n\r\f\t";

    //DML
    public static boolean isSelectStatement(String sql) {
        String sql0 = sql.trim();
        // with xx as ( select x ...
        if (Strings.startsWithIgnoreCase(sql0, "with")) {
            StringTokenizer stringTokenizer = new StringTokenizer(sql0);
            int i = 0;
            while (i < 5 && stringTokenizer.hasMoreTokens()) {
                String token = stringTokenizer.nextToken();
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


}
