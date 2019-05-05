package com.fjn.helper.sql.dialect.internal.urlparser;

import com.fjn.helper.sql.dialect.DatabaseInfo;
import com.fjn.helper.sql.util.StringMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JtdsUrlParser implements UrlParser {
    public static final int DEFAULT_PORT = 1433;
    static final String URL_PREFIX = "jdbc:jtds:sqlserver:";
    private static final Logger logger = LoggerFactory.getLogger(JtdsUrlParser.class);
    private static final List<String> URL_SCHEMAS = Arrays.asList(new String[]{URL_PREFIX});

    public JtdsUrlParser() {
    }

    @Override
    public List<String> getUrlSchemas() {
        return URL_SCHEMAS;
    }

    @Override
    public DatabaseInfo parse(final String jdbcUrl) {
        if (jdbcUrl == null) {
            logger.info("jdbcUrl may not be null");
            return UnKnownDatabaseInfo.INSTANCE;
        }
        if (!jdbcUrl.startsWith("jdbc:jtds:sqlserver:")) {
            logger.info("jdbcUrl has invalid prefix.(url:{}, prefix:{})", (Object) jdbcUrl, (Object) "jdbc:jtds:sqlserver:");
            return UnKnownDatabaseInfo.INSTANCE;
        }
        DatabaseInfo result = null;
        try {
            result = this.parse0(jdbcUrl);
        } catch (Exception e) {
            logger.info("JtdsJdbcUrl parse error. url: {}, Caused: {}", new Object[]{jdbcUrl, e.getMessage(), e});
            result = UnKnownDatabaseInfo.createUnknownDataBase("sqlserver", jdbcUrl);
        }
        return result;
    }

    private DatabaseInfo parse0(final String url) {
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
