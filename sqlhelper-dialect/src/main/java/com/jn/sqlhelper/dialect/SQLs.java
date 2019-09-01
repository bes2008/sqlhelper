package com.jn.sqlhelper.dialect;

import com.jn.langx.util.Strings;

import java.util.StringTokenizer;

public class SQLs {
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
}
