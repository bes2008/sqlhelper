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
import com.fjn.helper.sql.util.StringMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MySqlUrlParser implements UrlParser {
    private static final String URL_PREFIX = "jdbc:mysql:";
    private static final String LOADBALANCE_URL_PREFIX = "jdbc:mysql:loadbalance:";
    private static final Logger logger = LoggerFactory.getLogger(MySqlUrlParser.class);
    private static final List<String> URL_SCHEMAS = Arrays.asList(new String[]{URL_PREFIX});

    @Override
    public List<String> getUrlSchemas() {
        return URL_SCHEMAS;
    }

    public MySqlUrlParser() {

    }

    @Override
    public DatabaseInfo parse(final String jdbcUrl) {
        if (jdbcUrl == null) {
            logger.info("jdbcUrl may not be null");
            return UnKnownDatabaseInfo.INSTANCE;
        }
        if (!jdbcUrl.startsWith("jdbc:mysql:")) {
            logger.info("jdbcUrl has invalid prefix.(url:{}, prefix:{})", (Object) jdbcUrl, (Object) "jdbc:mysql:");
            return UnKnownDatabaseInfo.INSTANCE;
        }
        DatabaseInfo result = null;
        try {
            result = this.parse0(jdbcUrl);
        } catch (Exception e) {
            logger.info("MySqlJdbcUrl parse error. url: {}, Caused: {}", new Object[]{jdbcUrl, e.getMessage(), e});
            result = UnKnownDatabaseInfo.createUnknownDataBase("mysql", jdbcUrl);
        }
        return result;
    }

    private DatabaseInfo parse0(final String jdbcUrl) {
        if (this.isLoadbalanceUrl(jdbcUrl)) {
            return this.parseLoadbalancedUrl(jdbcUrl);
        }
        return this.parseNormal(jdbcUrl);
    }

    private boolean isLoadbalanceUrl(final String jdbcUrl) {
        return jdbcUrl.regionMatches(true, 0, "jdbc:mysql:loadbalance:", 0, "jdbc:mysql:loadbalance:".length());
    }

    private DatabaseInfo parseLoadbalancedUrl(final String jdbcUrl) {
        final StringMaker maker = new StringMaker(jdbcUrl);
        maker.after("jdbc:mysql:");
        final String host = maker.after("//").before('/').value();
        final String[] parsedHost = host.split(",");
        final List<String> hostList = Arrays.asList(parsedHost);
        final String databaseId = maker.next().afterLast('/').before('?').value();
        final String normalizedUrl = maker.clear().before('?').value();
        return new DefaultDatabaseInfo("mysql", jdbcUrl, normalizedUrl, hostList, databaseId);
    }

    private DatabaseInfo parseNormal(final String jdbcUrl) {
        final StringMaker maker = new StringMaker(jdbcUrl);
        maker.after("jdbc:mysql:");
        final String host = maker.after("//").before('/').value();
        final List<String> hostList = new ArrayList<String>(1);
        hostList.add(host);
        final String databaseId = maker.next().afterLast('/').before('?').value();
        final String normalizedUrl = maker.clear().before('?').value();
        return new DefaultDatabaseInfo("mysql", jdbcUrl, normalizedUrl, hostList, databaseId);
    }
}
