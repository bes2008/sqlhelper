package com.jn.sqlhelper.common.utils;

import com.jn.langx.util.Strings;

public class Tables {
    public static String getTableFQN(String catalog, String schema, String tableName) {
        return getTableFQN(catalog, schema, tableName, null);
    }

    public static String getTableFQN(String catalog, String schema, String tableName, String separator) {
        if (Strings.isEmpty(separator)) {
            separator = ".";
        }
        String fqn = tableName;
        if (Strings.isNotEmpty(schema)) {
            fqn = schema + separator + fqn;
        }
        if (Strings.isNotEmpty(catalog)) {
            fqn = catalog + separator + fqn;
        }
        return fqn;
    }
}
