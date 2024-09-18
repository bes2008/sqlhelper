package com.jn.sqlhelper.dialect.urlparser;

import com.jn.sqlhelper.dialect.urlparser.oracle.Description;
import com.jn.sqlhelper.dialect.urlparser.oracle.KeyValue;
import com.jn.sqlhelper.dialect.urlparser.oracle.OracleNetConnectionDescriptorParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OracleUrlParser extends CommonUrlParser {
    private static final String URL_PREFIX = "jdbc:oracle:";
    private static final List<String> URL_SCHEMAS = Arrays.asList(new String[]{URL_PREFIX});

    @Override
    public List<String> getUrlSchemas() {
        return URL_SCHEMAS;
    }

    public OracleUrlParser() {

    }

    @Override
    public String getName() {
        return "oracle";
    }

    @Override
    protected DatabaseInfo parse0(final String jdbcUrl, String urlPrefix) {
        final StringMaker maker = new StringMaker(jdbcUrl);
        maker.after("jdbc:oracle:").after(":");
        final String description = maker.after('@').value().trim();
        if (description.startsWith("(")) {
            return this.parseNetConnectionUrl(jdbcUrl);
        }
        return this.parseSimpleUrl(jdbcUrl, maker);
    }

    private DatabaseInfo parseNetConnectionUrl(final String url) {
        final OracleNetConnectionDescriptorParser parser = new OracleNetConnectionDescriptorParser(url);
        final KeyValue keyValue = parser.parse();
        return this.createOracleDatabaseInfo(keyValue, url);
    }

    private DatabaseInfo createOracleDatabaseInfo(final KeyValue keyValue, final String url) {
        final Description description = new Description(keyValue);
        final List<String> jdbcHost = description.getJdbcHost();
        return new DefaultDatabaseInfo("oracle", url, url, jdbcHost, description.getDatabaseId());
    }

    private DefaultDatabaseInfo parseSimpleUrl(final String url, final StringMaker maker) {
        final String host = maker.before(':').value();
        final String port = maker.next().after(':').before(':', '/').value();
        final String databaseId = maker.next().afterLast(':', '/').value();
        final List<String> hostList = new ArrayList<String>(1);
        hostList.add(host + ":" + port);
        return new DefaultDatabaseInfo("oracle", url, url, hostList, databaseId);
    }
}
