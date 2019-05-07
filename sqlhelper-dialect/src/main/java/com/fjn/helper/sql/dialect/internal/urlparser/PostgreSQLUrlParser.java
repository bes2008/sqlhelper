
/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the LGPL, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fjn.helper.sql.dialect.internal.urlparser;

import com.fjn.helper.sql.dialect.DatabaseInfo;
import com.fjn.helper.sql.util.StringMaker;
import org.slf4j.*;
import java.util.*;

public class PostgreSQLUrlParser implements UrlParser
{
    private static final String URL_PREFIX = "jdbc:postgresql:";
    private static final String LOADBALANCE_URL_PREFIX = "jdbc:postgresql:loadbalance:";
    private static final Logger logger = LoggerFactory.getLogger(PostgreSQLUrlParser.class);
    private static final List<String> URL_SCHEMAS = Arrays.asList(new String[]{URL_PREFIX});

    @Override
    public List<String> getUrlSchemas() {
        return URL_SCHEMAS;
    }


    public PostgreSQLUrlParser() {
    }
    
    @Override
    public DatabaseInfo parse(final String jdbcUrl) {
        if (jdbcUrl == null) {
            logger.info("jdbcUrl may not be null");
            return UnKnownDatabaseInfo.INSTANCE;
        }
        if (!jdbcUrl.startsWith("jdbc:postgresql:")) {
            logger.info("jdbcUrl has invalid prefix.(url:{}, prefix:{})", (Object)jdbcUrl, (Object)"jdbc:postgresql:");
            return UnKnownDatabaseInfo.INSTANCE;
        }
        DatabaseInfo result = null;
        try {
            result = this.parse0(jdbcUrl);
        }
        catch (Exception e) {
            logger.info("PostgreJdbcUrl parse error. url: {}, Caused: {}", new Object[] { jdbcUrl, e.getMessage(), e });
            result = UnKnownDatabaseInfo.createUnknownDataBase("postgresql", jdbcUrl);
        }
        return result;
    }
    
    private DatabaseInfo parse0(final String url) {
        if (this.isLoadbalanceUrl(url)) {
            return this.parseLoadbalancedUrl(url);
        }
        return this.parseNormal(url);
    }
    
    private DatabaseInfo parseLoadbalancedUrl(final String url) {
        final StringMaker maker = new StringMaker(url);
        maker.after("jdbc:postgresql:");
        final String host = maker.after("//").before('/').value();
        final String[] parsedHost = host.split(",");
        final List<String> hostList = Arrays.asList(parsedHost);
        final String databaseId = maker.next().afterLast('/').before('?').value();
        final String normalizedUrl = maker.clear().before('?').value();
        return new DefaultDatabaseInfo("postgresql", url, normalizedUrl, hostList, databaseId);
    }
    
    private boolean isLoadbalanceUrl(final String url) {
        return url.regionMatches(true, 0, "jdbc:postgresql:loadbalance:", 0, "jdbc:postgresql:loadbalance:".length());
    }
    
    private DatabaseInfo parseNormal(final String url) {
        final StringMaker maker = new StringMaker(url);
        maker.after("jdbc:postgresql:");
        final String host = maker.after("//").before('/').value();
        final List<String> hostList = new ArrayList<String>(1);
        hostList.add(host);
        final String databaseId = maker.next().afterLast('/').before('?').value();
        final String normalizedUrl = maker.clear().before('?').value();
        return new DefaultDatabaseInfo("postgresql", url, normalizedUrl, hostList, databaseId);
    }
}
