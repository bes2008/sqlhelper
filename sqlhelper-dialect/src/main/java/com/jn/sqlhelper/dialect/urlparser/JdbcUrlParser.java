package com.jn.sqlhelper.dialect.urlparser;

import com.jn.langx.util.Objs;
import com.jn.langx.util.Preconditions;
import com.jn.sqlhelper.dialect.Dialect;
import com.jn.sqlhelper.dialect.DialectRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class JdbcUrlParser {
    private static final Logger logger = LoggerFactory.getLogger(JdbcUrlParser.class);
    private DialectRegistry dialectRegistry = DialectRegistry.getInstance();

    public DatabaseInfo parse(final String url) {
        Preconditions.checkNotNull(url);
        Collection<Dialect> dialects = dialectRegistry.getDialects();
        UrlParser parser = null;

        for (Dialect dialect : dialects) {
            if (Objs.isNotNull(dialect.getUrlParser())) {
                for (String schema : dialect.getUrlParser().getUrlSchemas()) {
                    if (url.startsWith(schema)) {
                        parser = dialect.getUrlParser();
                        break;
                    }
                }
            }
        }
        if (parser != null) {
            try {
                return parser.parse(url);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return UnKnownDatabaseInfo.createUnknownDataBase(url);
            }
        } else {
            return UnKnownDatabaseInfo.createUnknownDataBase(url);
        }
    }
}
