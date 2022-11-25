
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

package com.jn.sqlhelper.dialect.internal.urlparser;

import com.jn.sqlhelper.dialect.urlparser.DatabaseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CubridUrlParser extends CommonUrlParser {
    public static final String DEFAULT_HOSTNAME = "localhost";
    public static final int DEFAULT_PORT = 30000;
    public static final String DEFAULT_USER = "public";
    public static final String DEFAULT_PASSWORD = "";
    private static final String URL_PREFIX_PATTERN = "jdbc:cubrid(-oracle|-mysql)?:";
    private static final Pattern PREFIX_PATTERN;

    private static final String URL_PATTERN = "jdbc:cubrid(-oracle|-mysql)?:([a-zA-Z_0-9\\.-]*):([0-9]*):([^:]+):([^:]*):([^:]*):(\\?[a-zA-Z_0-9]+=[^&=?]+(&[a-zA-Z_0-9]+=[^&=?]+)*)?";
    private static final Pattern PATTERN;
    private final static Logger logger = LoggerFactory.getLogger(CubridUrlParser.class);
    private static final String URL_PREFIX = "jdbc:cubrid";
    private static final List<String> URL_SCHEMAS = Arrays.asList(new String[]{URL_PREFIX});

    @Override
    public List<String> getUrlSchemas() {
        return URL_SCHEMAS;
    }

    public CubridUrlParser() {
    }

    @Override
    public DatabaseInfo parse(final String jdbcUrl) {
        final Matcher matcher = CubridUrlParser.PREFIX_PATTERN.matcher(jdbcUrl);
        if (!matcher.find()) {
            logger.info("jdbcUrl has invalid prefix.(url:{}, prefix-pattern:{})", (Object) jdbcUrl, (Object) "jdbc:cubrid(-oracle|-mysql)?:");
            return UnKnownDatabaseInfo.INSTANCE;
        }
        DatabaseInfo result = null;
        try {
            result = this.parse0(jdbcUrl);
        } catch (Exception e) {
            logger.info("CubridJdbcUrl parse error. url: {}, Caused: {}", new Object[]{jdbcUrl, e.getMessage(), e});
            result = UnKnownDatabaseInfo.createUnknownDataBase("cubrid", jdbcUrl);
        }
        return result;
    }

    private DatabaseInfo parse0(final String jdbcUrl) {
        final Matcher matcher = CubridUrlParser.PATTERN.matcher(jdbcUrl);
        if (!matcher.find()) {
            throw new IllegalArgumentException();
        }
        String host = matcher.group(2);
        final String portString = matcher.group(3);
        final String db = matcher.group(4);
        String user = matcher.group(5);
        if (host == null || host.length() == 0) {
            host = "localhost";
        }
        if (user == null) {
            user = "public";
        }
        final StringMaker maker = new StringMaker(jdbcUrl);
        final String normalizedUrl = maker.clear().before('?').value();
        final List<String> hostList = new ArrayList<String>(1);
        final String hostAndPort = host + ":" + portString;
        hostList.add(hostAndPort);
        return new DefaultDatabaseInfo("cubrid", jdbcUrl, normalizedUrl, hostList, db);
    }

    static {
        PREFIX_PATTERN = Pattern.compile("jdbc:cubrid(-oracle|-mysql)?:", 2);
        PATTERN = Pattern.compile("jdbc:cubrid(-oracle|-mysql)?:([a-zA-Z_0-9\\.-]*):([0-9]*):([^:]+):([^:]*):([^:]*):(\\?[a-zA-Z_0-9]+=[^&=?]+(&[a-zA-Z_0-9]+=[^&=?]+)*)?", 2);
    }
}
