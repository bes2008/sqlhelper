
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

package com.github.fangjinuo.sqlhelper.dialect.internal.urlparser;

import com.github.fangjinuo.sqlhelper.dialect.DatabaseInfo;
import com.github.fangjinuo.sqlhelper.util.StringMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MariaDBUrlParser implements UrlParser
{
    private static final String URL_PREFIX = "jdbc:mariadb:";
    private static final String MYSQL_URL_PREFIX = "jdbc:mysql:";
    private static final Logger logger =LoggerFactory.getLogger(MariaDBUrlParser.class) ;
    private static final List<String> URL_SCHEMAS = Arrays.asList(new String[]{URL_PREFIX});

    @Override
    public List<String> getUrlSchemas() {
        return URL_SCHEMAS;
    }
    public MariaDBUrlParser() {
    }
    

    
    private DatabaseInfo parse0(final String url, final Type type) {
        if (this.isLoadbalanceUrl(url, type)) {
            return this.parseLoadbalancedUrl(url, type);
        }
        return this.parseNormal(url, type);
    }
    
    private boolean isLoadbalanceUrl(final String url, final Type type) {
        final String loadbalanceUrlPrefix = type.getLoadbalanceUrlPrefix();
        return url.regionMatches(true, 0, loadbalanceUrlPrefix, 0, loadbalanceUrlPrefix.length());
    }
    
    private DatabaseInfo parseLoadbalancedUrl(final String url, final Type type) {
        final StringMaker maker = new StringMaker(url);
        maker.after(type.getUrlPrefix());
        final String host = maker.after("//").before('/').value();
        final String[] parsedHost = host.split(",");
        final List<String> hostList = Arrays.asList(parsedHost);
        final String databaseId = maker.next().afterLast('/').before('?').value();
        final String normalizedUrl = maker.clear().before('?').value();
        return new DefaultDatabaseInfo("mariadb", url, normalizedUrl, hostList, databaseId);
    }
    
    private DatabaseInfo parseNormal(final String url, final Type type) {
        final StringMaker maker = new StringMaker(url);
        maker.after(type.getUrlPrefix());
        final String host = maker.after("//").before('/').value();
        final List<String> hostList = new ArrayList<String>(1);
        hostList.add(host);
        final String databaseId = maker.next().afterLast('/').before('?').value();
        final String normalizedUrl = maker.clear().before('?').value();
        return new DefaultDatabaseInfo("mariadb", url, normalizedUrl, hostList, databaseId);
    }
    
    public enum Type
    {
        MARIA("jdbc:mariadb:"), 
        MYSQL("jdbc:mysql:");
        
        private final String urlPrefix;
        private final String loadbalanceUrlPrefix;
        
        private Type(final String urlPrefix) {
            this.urlPrefix = urlPrefix;
            this.loadbalanceUrlPrefix = urlPrefix + "loadbalance:";
        }
        
        private String getUrlPrefix() {
            return this.urlPrefix;
        }
        
        private String getLoadbalanceUrlPrefix() {
            return this.urlPrefix + "loadbalance:";
        }
        
        public static Type findType(final String jdbcUrl) {
            for (final Type type : values()) {
                if (jdbcUrl.startsWith(type.urlPrefix)) {
                    return type;
                }
            }
            return null;
        }
    }

    @Override
    public DatabaseInfo parse(final String jdbcUrl) {
        if (jdbcUrl == null) {
            logger.info("jdbcUrl may not be null");
            return UnKnownDatabaseInfo.INSTANCE;
        }
        final Type type = Type.findType(jdbcUrl);
        if (type == null) {
            logger.info("jdbcUrl has invalid prefix.(url:{}, prefix:{}, {})", new Object[] { jdbcUrl, "jdbc:mariadb:", "jdbc:mysql:" });
            return UnKnownDatabaseInfo.INSTANCE;
        }
        DatabaseInfo result = null;
        try {
            result = this.parse0(jdbcUrl, type);
        }
        catch (Exception e) {
            logger.info("MaridDBJdbcUrl parse error. url: {}, Caused: {}", new Object[] { jdbcUrl, e.getMessage(), e });
            result = UnKnownDatabaseInfo.createUnknownDataBase("mariadb", jdbcUrl);
        }
        return result;
    }
}
