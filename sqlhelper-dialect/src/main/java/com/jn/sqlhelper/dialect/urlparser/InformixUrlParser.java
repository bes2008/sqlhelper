package com.jn.sqlhelper.dialect.urlparser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InformixUrlParser extends CommonUrlParser {
    private static final String URL_PREFIX = "jdbc:informix-sqli:";

    public InformixUrlParser() {

    }

    @Override
    public String getName() {
        return "informix";
    }

    private static final List<String> URL_SCHEMAS = Arrays.asList(new String[]{URL_PREFIX});

    @Override
    public List<String> getUrlSchemas() {
        return URL_SCHEMAS;
    }

    @Override
    protected DatabaseInfo parse0(final String url, String urlPrefix) {
        final StringMaker maker = new StringMaker(url);
        maker.after("jdbc:informix-sqli:");
        final String host = maker.after("//").before('/').value();
        final List<String> hostList = new ArrayList<String>(1);
        hostList.add(host);
        final String databaseId = maker.next().afterLast('/').before(':').value();
        final String normalizedUrl = maker.clear().before(':').value();
        return new DefaultDatabaseInfo("informix-sqli", url, normalizedUrl, hostList, databaseId);
    }
}
