package com.jn.sqlhelper.dialect.urlparser;

import java.util.ArrayList;
import java.util.List;

import static com.jn.sqlhelper.dialect.urlparser.DatabaseInfo.UNKNOWN;

public class UnKnownDatabaseInfo {
    public static final DatabaseInfo INSTANCE;

    public static DatabaseInfo createUnknownDataBase(final String url) {
        return createUnknownDataBase(null, url);
    }

    public static DatabaseInfo createUnknownDataBase(final String vendor, final String url) {
        final List<String> list = new ArrayList<String>();
        list.add(UNKNOWN);
        return new DefaultDatabaseInfo(vendor, url, url, list, UNKNOWN, false);
    }

    static {
        final List<String> urls = new ArrayList<String>();
        urls.add(UNKNOWN);
        INSTANCE = new DefaultDatabaseInfo(UNKNOWN, UNKNOWN, UNKNOWN, urls, UNKNOWN, false);
    }
}
