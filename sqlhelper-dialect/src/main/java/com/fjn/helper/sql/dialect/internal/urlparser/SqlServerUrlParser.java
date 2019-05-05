package com.fjn.helper.sql.dialect.internal.urlparser;

import com.fjn.helper.sql.dialect.DatabaseInfo;
import com.fjn.helper.sql.util.StringMaker;
import org.slf4j.*;
import java.util.*;

public class SqlServerUrlParser implements UrlParser
{
    public static final int DEFAULT_PORT = 1433;
    private static final String URL_PREFIX = "jdbc:sqlserver:";
    private static final Logger logger = LoggerFactory.getLogger(SqlServerUrlParser.class);
    private static final List<String> URL_SCHEMAS = Arrays.asList(new String[]{URL_PREFIX, JtdsUrlParser.URL_PREFIX});

    @Override
    public List<String> getUrlSchemas() {
        return URL_SCHEMAS;
    }


    public SqlServerUrlParser() {

    }
    
    @Override
    public DatabaseInfo parse(final String jdbcUrl) {
        if (jdbcUrl == null) {
            logger.info("jdbcUrl may not be null");
            return UnKnownDatabaseInfo.INSTANCE;
        }

        if (!jdbcUrl.startsWith("jdbc:sqlserver:")) {
            if(jdbcUrl.startsWith(JtdsUrlParser.URL_PREFIX)){
                return new JtdsUrlParser().parse(jdbcUrl);
            }
            logger.info("jdbcUrl has invalid prefix.(url:{}, prefix:{})", (Object)jdbcUrl, (Object)"jdbc:sqlserver:");
            return UnKnownDatabaseInfo.INSTANCE;
        }
        DatabaseInfo result = null;
        try {
            result = this.parse0(jdbcUrl);
        }
        catch (Exception e) {
            logger.info("JtdsJdbcUrl parse error. url: {}, Caused: {}", new Object[] { jdbcUrl, e.getMessage(), e });
            result = UnKnownDatabaseInfo.createUnknownDataBase("sqlserver", jdbcUrl);
        }
        return result;
    }
    
    private DatabaseInfo parse0(final String url) {
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
