package com.jn.sqlhelper.dialect.urlparser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SqlServerUrlParser extends CommonUrlParser {
    public static final int DEFAULT_PORT = 1433;
    private static final String URL_PREFIX = "jdbc:sqlserver:";
    private static final List<String> URL_SCHEMAS = Arrays.asList(new String[]{URL_PREFIX, JtdsUrlParser.URL_PREFIX});

    @Override
    public List<String> getUrlSchemas() {
        return URL_SCHEMAS;
    }


    public SqlServerUrlParser() {

    }

    @Override
    protected DatabaseInfo parse0(final String url, String urlPrefix) {
        final StringMaker maker = new StringMaker(url);
        maker.lower().after("jdbc:sqlserver:");
        final StringMaker before = maker.after("//").before(';');
        final String hostAndPortString = before.value();
        String databaseId = "";
        final List<String> hostList = new ArrayList<String>(1);
        hostList.add(hostAndPortString);
        if (databaseId.isEmpty()) {
            databaseId = maker.next().after("databasename=").before(';').value();
        }
        final String normalizedUrl = maker.clear().before(";").value();
        return new DefaultDatabaseInfo("sqlserver", url, normalizedUrl, hostList, databaseId);
    }
}
