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

package com.jn.sqlhelper.dialect.urlparser;

import com.jn.langx.util.Objs;
import com.jn.langx.util.Preconditions;
import com.jn.sqlhelper.dialect.Dialect;
import com.jn.sqlhelper.dialect.DialectRegistry;
import com.jn.sqlhelper.dialect.internal.urlparser.UnKnownDatabaseInfo;
import com.jn.sqlhelper.dialect.internal.urlparser.UrlParser;
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
