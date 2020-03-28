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

import com.jn.sqlhelper.dialect.Dialect;
import com.jn.sqlhelper.dialect.DialectRegistry;
import com.jn.sqlhelper.dialect.internal.urlparser.UnKnownDatabaseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class JdbcUrlParser {
    private static final Logger logger = LoggerFactory.getLogger(JdbcUrlParser.class);
    private DialectRegistry dialectRegistry = DialectRegistry.getInstance();

    public DatabaseInfo parse(String url) {
        Collection<Dialect> dialects = dialectRegistry.getDialects();
        Dialect d = null;
        for (Dialect dialect : dialects) {
            for (String schema : dialect.getUrlSchemas()) {
                if (url.startsWith(schema)) {
                    d = dialect;
                    break;
                }
            }
        }
        if (d != null) {
            try {
                return d.parse(url);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return UnKnownDatabaseInfo.createUnknownDataBase(url);
            }
        } else {
            return UnKnownDatabaseInfo.createUnknownDataBase(url);
        }
    }
}
