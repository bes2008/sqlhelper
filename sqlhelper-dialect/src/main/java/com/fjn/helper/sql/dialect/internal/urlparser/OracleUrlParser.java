/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the LGPL, Version 2.1 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fjn.helper.sql.dialect.internal.urlparser;

import com.fjn.helper.sql.dialect.DatabaseInfo;
import com.fjn.helper.sql.dialect.internal.urlparser.oracle.Description;
import com.fjn.helper.sql.dialect.internal.urlparser.oracle.KeyValue;
import com.fjn.helper.sql.dialect.internal.urlparser.oracle.OracleNetConnectionDescriptorParser;
import com.fjn.helper.sql.util.StringMaker;
import org.slf4j.*;
import java.util.*;

public class OracleUrlParser implements UrlParser
{
    private static final String URL_PREFIX = "jdbc:oracle:";
    private static final Logger logger = LoggerFactory.getLogger(OracleUrlParser.class);
    private static final List<String> URL_SCHEMAS = Arrays.asList(new String[]{URL_PREFIX});

    @Override
    public List<String> getUrlSchemas() {
        return URL_SCHEMAS;
    }
    
    public OracleUrlParser() {

    }
    
    @Override
    public DatabaseInfo parse(final String jdbcUrl) {
        if (jdbcUrl == null) {
            logger.info("jdbcUrl may not be null");
            return UnKnownDatabaseInfo.INSTANCE;
        }
        if (!jdbcUrl.startsWith("jdbc:oracle:")) {
            logger.info("jdbcUrl has invalid prefix.(url:{}, prefix:{})", (Object)jdbcUrl, (Object)"jdbc:oracle:");
            return UnKnownDatabaseInfo.INSTANCE;
        }
        DatabaseInfo result = null;
        try {
            result = this.parse0(jdbcUrl);
        }
        catch (Exception e) {
            logger.info("OracleJdbcUrl parse error. url: {}, Caused: {}", new Object[] { jdbcUrl, e.getMessage(), e });
            result = UnKnownDatabaseInfo.createUnknownDataBase("oracle", jdbcUrl);
        }
        return result;
    }
    
    private DatabaseInfo parse0(final String jdbcUrl) {
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
