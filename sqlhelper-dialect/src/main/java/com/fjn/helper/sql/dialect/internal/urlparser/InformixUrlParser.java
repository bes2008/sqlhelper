package com.fjn.helper.sql.dialect.internal.urlparser;

import com.fjn.helper.sql.dialect.DatabaseInfo;
import com.fjn.helper.sql.util.StringMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InformixUrlParser implements UrlParser {
    private static final String URL_PREFIX = "jdbc:informix-sqli:";
    private static final Logger logger = LoggerFactory.getLogger(InformixUrlParser.class);

    public InformixUrlParser() {

    }

    private static final List<String> URL_SCHEMAS = Arrays.asList(new String[]{URL_PREFIX});

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
        if (!jdbcUrl.startsWith("jdbc:informix-sqli:")) {
            logger.info("jdbcUrl has invalid prefix.(url:{}, prefix:{})", (Object) jdbcUrl, (Object) "jdbc:informix-sqli:");
            return UnKnownDatabaseInfo.INSTANCE;
        }
        DatabaseInfo result = null;
        try {
            result = this.parse0(jdbcUrl);
        } catch (Exception e) {
            logger.info("JtdsJdbcUrl parse error. url: {}, Caused: {}", new Object[]{jdbcUrl, e.getMessage(), e});
            result = UnKnownDatabaseInfo.createUnknownDataBase("informix-sqli", jdbcUrl);
        }
        return result;
    }

    private DatabaseInfo parse0(final String url) {
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
