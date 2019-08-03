
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
import com.jn.sqlhelper.util.StringMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InformixUrlParser extends CommonUrlParser {
    private static final String URL_PREFIX = "jdbc:informix-sqli:";
    private static final Logger logger = LoggerFactory.getLogger(InformixUrlParser.class);

    public InformixUrlParser() {

    }

    private static final List<String> URL_SCHEMAS = Arrays.asList(new String[]{URL_PREFIX});

    @Override
    public List<String> getUrlSchemas() {
        return URL_SCHEMAS;
    }

    @Override
    protected DatabaseInfo parse0(final String url, String urlPrefix) {
        final StringMaker maker = new StringMaker(url);
        maker.after("jdbc:informix-sqli:");
        final String host = maker.after("//").before('/').value();
        final List<String> hostList = new ArrayList<String>(1);
        hostList.add(host);
        final String databaseId = maker.next().afterLast('/').before(':').value();
        final String normalizedUrl = maker.clear().before(':').value();
        return new DefaultDatabaseInfo("informix-sqli", url, normalizedUrl, hostList, databaseId);
    }
}
