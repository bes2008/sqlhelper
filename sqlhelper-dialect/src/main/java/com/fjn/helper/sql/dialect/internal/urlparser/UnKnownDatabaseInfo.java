package com.fjn.helper.sql.dialect.internal.urlparser;

import com.fjn.helper.sql.dialect.DatabaseInfo;

import java.util.ArrayList;
import java.util.List;

public class UnKnownDatabaseInfo {
    public static final DatabaseInfo INSTANCE;

    public static DatabaseInfo createUnknownDataBase(final String url) {
        final List<String> list = new ArrayList<String>();
        list.add("unknown");
        return createUnknownDataBase(null, url);
    }

    public static DatabaseInfo createUnknownDataBase(final String vendor, final String url) {
        final List<String> list = new ArrayList<String>();
        list.add("unknown");
        return new DefaultDatabaseInfo(vendor, url, url, list, "unknown", false);
    }

    static {
        final List<String> urls = new ArrayList<String>();
        urls.add("unknown");
        INSTANCE = new DefaultDatabaseInfo("JDBC", "unknown", "unknown", urls, "unknown", false);
    }
}
