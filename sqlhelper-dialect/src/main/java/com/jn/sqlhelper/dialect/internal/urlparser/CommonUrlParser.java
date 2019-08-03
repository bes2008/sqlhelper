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

import com.jn.sqlhelper.dialect.DatabaseInfo;
import com.jn.sqlhelper.dialect.Dialect;
import com.jn.sqlhelper.util.StringMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommonUrlParser implements UrlParser {
    private final Logger logger = LoggerFactory.getLogger(CommonUrlParser.class);
    private Dialect dialect;

    public Dialect getDialect() {
        return dialect;
    }

    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }

    @Override
    public DatabaseInfo parse(String jdbcUrl) {
        if (jdbcUrl == null) {
            this.logger.info("jdbcUrl may not be null");
            return UnKnownDatabaseInfo.INSTANCE;
        }
        List<String> urlPrefixes = getUrlSchemas();
        String matchedUrlPrefix = null;
        for(String urlPrefix : urlPrefixes){
            if (jdbcUrl.startsWith(urlPrefix)) {
                matchedUrlPrefix = urlPrefix;
            }
        }
        if(matchedUrlPrefix==null) {
            this.logger.info("jdbcUrl has invalid prefix.(url:{}, prefix:{})", (Object) jdbcUrl, urlPrefixes.toString());
            return UnKnownDatabaseInfo.INSTANCE;
        }
        DatabaseInfo result = null;
        try {
            result = this.parse0(jdbcUrl, matchedUrlPrefix);
        } catch (Exception e) {
            this.logger.info("{} parse error. url: {}, Caused: {}",  this.getClass().getSimpleName(),jdbcUrl, e.getMessage(), e);
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
        return new DefaultDatabaseInfo(getDialect().getDatabaseId(), url, normalizedUrl, hostList, databaseId);
    }

    @Override
    public List<String> getUrlSchemas() {
        return Collections.emptyList();
    }
}
