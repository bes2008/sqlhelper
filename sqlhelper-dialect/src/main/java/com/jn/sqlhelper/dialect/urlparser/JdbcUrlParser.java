package com.jn.sqlhelper.dialect.urlparser;

import com.jn.langx.registry.GenericRegistry;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.spi.CommonServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class JdbcUrlParser {
    private static final Logger logger = LoggerFactory.getLogger(JdbcUrlParser.class);


    private static final GenericRegistry<UrlParser> urlParserRegistry = new GenericRegistry<UrlParser>();

    static {
        Pipeline.of(new CommonServiceProvider<UrlParser>().get(UrlParser.class))
                .forEach(new Consumer<UrlParser>() {
                    @Override
                    public void accept(UrlParser urlParser) {
                        urlParserRegistry.register(urlParser);
                    }
                });
    }

    private JdbcUrlParser(){

    }
    public static final JdbcUrlParser INSTANCE = new JdbcUrlParser();
    public DatabaseInfo parse(final String url) {
        Preconditions.checkNotNull(url);
        List<UrlParser> dialects = urlParserRegistry.instances();
        UrlParser parser = null;

        for ( UrlParser p : dialects) {
            if(!Objs.isEmpty(p.getUrlSchemas())) {
                for (String schema : p.getUrlSchemas()) {
                    if (url.startsWith(schema)) {
                        parser = p;
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
