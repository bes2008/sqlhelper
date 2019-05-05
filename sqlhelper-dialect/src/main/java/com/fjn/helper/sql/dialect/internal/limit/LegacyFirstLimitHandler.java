package com.fjn.helper.sql.dialect.internal.limit;

import com.fjn.helper.sql.dialect.RowSelection;

import java.util.Locale;


public class LegacyFirstLimitHandler
        extends AbstractLimitHandler {
    public static final LegacyFirstLimitHandler INSTANCE = new LegacyFirstLimitHandler();


    public String processSql(String sql, RowSelection selection) {
        return new StringBuilder(sql.length() + 16).append(sql).insert(sql.toLowerCase(Locale.ROOT).indexOf("select") + 6, " first " + getMaxOrLimit(selection)).toString();
    }
}
