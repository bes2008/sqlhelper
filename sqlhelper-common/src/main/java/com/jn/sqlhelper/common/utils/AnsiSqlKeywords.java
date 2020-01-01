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


import com.jn.langx.util.collection.Collects;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Maintains the set of ANSI SQL keywords
 *
 * @author Steve Ebersole
 */
public class AnsiSqlKeywords {
    /**
     * Singleton access
     */
    public static final AnsiSqlKeywords INSTANCE = new AnsiSqlKeywords();

    private final Set<String> keywordsSql2003;

    public AnsiSqlKeywords() {
        final Set<String> keywordsSql2003 = new HashSet<String>();
        Collects.addAll(keywordsSql2003, "ADD", "ALL", "ALLOCATE", "ALTER", "AND", "ANY",
                "ARE", "ARRAY", "AS", "ASC", "ASENSITIVE",
                "ASYMMETRIC", "AT", "ATOMIC", "AUTHORIZATION");
        Collects.addAll(keywordsSql2003, "BEGIN", "BETWEEN", "BIGINT", "BINARY", "BLOB", "BOTH", "BY");
        Collects.addAll(keywordsSql2003, "CALL", "CALLED", "CASCADED", "CASE", "CHAR",
                "CHARACTER", "CHECK", "CLOB", "CLOSE", "COLLATE",
                "COLUMN", "COMMIT", "CONDITION", "CONNECT", "CONSTRAINT",
                "CONTINUE", "CORRESPONDING", "CREATE", "CROSS", "CUBE",
                "CURRENT", "CURRENT_DATE", "CURRENT_PATH", "CURRENT_ROLE", "CURRENT_TIME",
                "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", "CYCLE");
        Collects.addAll(keywordsSql2003, "DATE", "DAY", "DEALLOCATE", "DEC", "DECIMAL",
                "DECLARE", "DEFAULT", "DELETE", "DEREF", "DESCRIBE",
                "DETERMINISTIC", "DISCONNECT", "DISTINCT", "DO", "DOUBLE",
                "DROP", "DYNAMIC");
        Collects.addAll(keywordsSql2003, "EACH", "ELEMENT", "ELSE", "ELSIF", "END",
                "ESCAPE", "EXCEPT", "EXEC", "EXECUTE", "EXISTS",
                "EXIT", "EXTERNAL");
        Collects.addAll(keywordsSql2003, "FALSE", "FETCH", "FILTER", "FLOAT", "FOR",
                "FOREIGN", "FREE", "FROM", "FULL", "FUNCTION");
        Collects.addAll(keywordsSql2003, "GET", "GLOBAL", "GRANT", "GROUP", "GROUPING");
        Collects.addAll(keywordsSql2003, "HANDLER", "HAVING", "HOLD", "HOUR");
        Collects.addAll(keywordsSql2003, "IDENTITY", "IF", "IMMEDIATE", "IN", "INDICATOR",
                "INNER", "INOUT", "INPUT", "INSENSITIVE", "INSERT",
                "INT", "INTEGER", "INTERSECT", "INTERVAL", "INTO",
                "IS", "ITERATE", "IS");
        keywordsSql2003.add("JOIN");
        Collects.addAll(keywordsSql2003, "LANGUAGE", "LARGE", "LATERAL", "LEADING", "LEAVE",
                "LEFT", "LIKE", "LOCAL", "LOCALTIME", "LOCALTIMESTAMP", "LOOP");
        Collects.addAll(keywordsSql2003, "MATCH", "MEMBER", "MERGE", "METHOD", "MINUTE",
                "MODIFIES", "MODULE", "MONTH", "MULTISET");
        Collects.addAll(keywordsSql2003, "NATIONAL", "NATURAL", "NCHAR", "NCLOB", "NEW",
                "NO", "NONE", "NOT", "NULL", "NUMERIC");
        Collects.addAll(keywordsSql2003, "OF", "OLD", "ON", "ONLY", "OPEN",
                "OR", "ORDER", "OUT", "OUTER", "OUTPUT",
                "OVER", "OVERLAPS");

        keywordsSql2003.add("OVER");
        keywordsSql2003.add("OVERLAPS");
        Collects.addAll(keywordsSql2003, "PARAMETER", "PARTITION", "PRECISION", "PREPARE", "PRIMARY", "PROCEDURE");
        Collects.addAll(keywordsSql2003, "RANGE", "READS", "REAL", "RECURSIVE", "REF",
                "REFERENCES", "REFERENCING", "RELEASE", "REPEAT", "RESIGNAL",
                "RESULT", "SOME", "RETURN", "RETURNS", "RIGHT",
                "ROLLBACK", "ROLLUP", "ROW", "ROWS");
        Collects.addAll(keywordsSql2003, "SAVEPOINT", "SCROLL", "SEARCH", "SECOND", "SELECT",
                "SENSITIVE", "SESSION_USE", "SET", "SIGNAL", "SIMILAR",
                "SMALLINT", "SOME", "SPECIFIC", "SPECIFICTYPE", "SQL",
                "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "START", "STATIC",
                "SUBMULTISET", "SYMMETRIC", "SYSTEM", "SYSTEM_USER");
        Collects.addAll(keywordsSql2003, "TABLE", "TABLESAMPLE", "THEN", "TIME", "TIMESTAMP",
                "TIMEZONE_HOUR", "TIMEZONE_MINUTE", "TO", "TRAILING", "TRANSLATION",
                "TREAT", "TRIGGER", "TRUE");
        Collects.addAll(keywordsSql2003, "UNDO", "UNION", "UNIQUE", "UNKNOWN", "UNNEST",
                "UNTIL", "UPDATE", "USER", "USING");
        Collects.addAll(keywordsSql2003, "VALUE", "VALUES", "VARCHAR", "VARYING");
        Collects.addAll(keywordsSql2003, "WHEN", "WHENEVER", "WHERE", "WHILE", "WINDOW",
                "WITH", "WITHIN", "WITHOUT");
        Collects.addAll(keywordsSql2003, "YEAR");
        this.keywordsSql2003 = Collections.unmodifiableSet(keywordsSql2003);
    }

    /**
     * Retrieve all keywords defined by ANSI SQL:2003
     *
     * @return ANSI SQL:2003 keywords
     */
    public Set<String> sql2003() {
        return keywordsSql2003;
    }


}
