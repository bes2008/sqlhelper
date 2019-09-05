package com.jn.sqlhelper.dialect;

import com.jn.langx.util.Strings;

import java.util.Locale;
import java.util.StringTokenizer;

public class SQLs {
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

    public static boolean isUpdateStatement(String sql){
        sql = sql.trim();
        return sql.toLowerCase(Locale.ROOT).replaceAll("\\s"," ").matches("update \\w+(\\.\\w)* set");
    }

    public static boolean isDeleteStatement(String sql){
        sql = sql.trim();
        return sql.toLowerCase(Locale.ROOT).replaceAll("\\s"," ").startsWith("delete from");
    }

    public static boolean isInsertStatement(String sql){
        sql = sql.trim();
        return sql.toLowerCase(Locale.ROOT).replaceAll("\\s"," ").startsWith("insert into");
    }


    // DDL
    public static boolean isCreateStatement(String sql){
        sql = sql.trim();
        return sql.toLowerCase(Locale.ROOT).replaceAll("\\s"," ").startsWith("create table");
    }

    public static boolean isDropTableStatement(String sql){
        sql = sql.trim();
        return sql.toLowerCase(Locale.ROOT).replaceAll("\\s"," ").startsWith("drop table");
    }

    public static boolean isQuote(String tok) {
        return "\"".equals(tok) ||
                "`".equals(tok) ||
                "]".equals(tok) ||
                "[".equals(tok) ||
                "'".equals(tok);
    }


}
