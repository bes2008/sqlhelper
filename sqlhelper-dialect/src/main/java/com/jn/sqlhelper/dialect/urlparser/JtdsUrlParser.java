package com.jn.sqlhelper.dialect.urlparser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JtdsUrlParser extends CommonUrlParser {
    public static final int DEFAULT_PORT = 1433;
    static final String URL_PREFIX = "jdbc:jtds:sqlserver:";
    private static final List<String> URL_SCHEMAS = Arrays.asList(new String[]{URL_PREFIX});

    public JtdsUrlParser() {
    }

    @Override
    public List<String> getUrlSchemas() {
        return URL_SCHEMAS;
    }

    @Override
    protected DatabaseInfo parse0(final String url, String urlPrefix) {
        final StringMaker maker = new StringMaker(url);
        maker.lower().after("jdbc:jtds:sqlserver:");
        final StringMaker before = maker.after("//").before(';');
        final String hostAndPortAndDataBaseString = before.value();
        String databaseId = "";
        String hostAndPortString = "";
        final int databaseIdIndex = hostAndPortAndDataBaseString.indexOf(47);
        if (databaseIdIndex != -1) {
            hostAndPortString = hostAndPortAndDataBaseString.substring(0, databaseIdIndex);
            databaseId = hostAndPortAndDataBaseString.substring(databaseIdIndex + 1, hostAndPortAndDataBaseString.length());
        } else {
            hostAndPortString = hostAndPortAndDataBaseString;
        }
        final List<String> hostList = new ArrayList<String>(1);
        hostList.add(hostAndPortString);
        if (databaseId.isEmpty()) {
            databaseId = maker.next().after("databasename=").before(';').value();
        }
        final String normalizedUrl = maker.clear().before(";").value();
        return new DefaultDatabaseInfo("sqlserver", url, normalizedUrl, hostList, databaseId);
    }
}
