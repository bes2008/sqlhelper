package com.jn.sqlhelper.dialect.urlparser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class CommonUrlParser implements UrlParser {
    private final Logger logger = LoggerFactory.getLogger(CommonUrlParser.class);

    @Override
    public DatabaseInfo parse(String jdbcUrl) {
        if (jdbcUrl == null) {
            this.logger.info("jdbcUrl may not be null");
            return UnKnownDatabaseInfo.INSTANCE;
        }
        List<String> urlPrefixes = getUrlSchemas();
        String matchedUrlPrefix = null;
        for (String urlPrefix : urlPrefixes) {
            if (jdbcUrl.startsWith(urlPrefix)) {
                matchedUrlPrefix = urlPrefix;
            }
        }
        if (matchedUrlPrefix == null) {
            this.logger.info("jdbcUrl has invalid prefix.(url:{}, prefix:{})", (Object) jdbcUrl, urlPrefixes.toString());
            return UnKnownDatabaseInfo.INSTANCE;
        }
        DatabaseInfo result = null;
        try {
            result = this.parse0(jdbcUrl, matchedUrlPrefix);
        } catch (Exception e) {
            this.logger.info("{} parse error. url: {}, Caused: {}", this.getClass().getSimpleName(), jdbcUrl, e.getMessage(), e);
            result = UnKnownDatabaseInfo.createUnknownDataBase("dm", jdbcUrl);
        }
        return result;
    }

    protected DatabaseInfo parse0(final String url, String urlPrefix) {
        final StringMaker maker = new StringMaker(url);
        maker.after(urlPrefix);
        final String host = maker.after("//").before('/').value();
        final List<String> hostList = new ArrayList<String>(1);
        hostList.add(host);
        final String databaseId = maker.next().afterLast('/').before('?').value();
        final String normalizedUrl = maker.clear().before('?').value();
        return new DefaultDatabaseInfo(getName(), url, normalizedUrl, hostList, databaseId);
    }

    @Override
    public List<String> getUrlSchemas() {
        return Collections.emptyList();
    }
}
