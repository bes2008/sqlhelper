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
        boolean hasCatalog = Strings.isEmpty(catalog);
        boolean hasSchema = Strings.isEmpty(schema);
        String fqn = tableName;
        if (hasSchema) {
            fqn = schema + separator + fqn;
        }
        if (hasCatalog) {
            fqn = catalog + separator + fqn;
        }
        return fqn;
    }
}
