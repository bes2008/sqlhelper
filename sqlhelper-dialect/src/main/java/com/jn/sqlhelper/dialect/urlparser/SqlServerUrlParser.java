
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

import com.jn.langx.util.StringMaker;
import com.jn.sqlhelper.dialect.DatabaseInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SqlServerUrlParser extends CommonUrlParser {
    public static final int DEFAULT_PORT = 1433;
    private static final String URL_PREFIX = "jdbc:sqlserver:";
    private static final List<String> URL_SCHEMAS = Arrays.asList(new String[]{URL_PREFIX, JtdsUrlParser.URL_PREFIX});

    @Override
    public List<String> getUrlSchemas() {
        return URL_SCHEMAS;
    }


    public SqlServerUrlParser() {

    }

    @Override
    protected DatabaseInfo parse0(final String url, String urlPrefix) {
        final StringMaker maker = new StringMaker(url);
        maker.lower().after("jdbc:sqlserver:");
        final StringMaker before = maker.after("//").before(';');
        final String hostAndPortString = before.value();
        String databaseId = "";
        final List<String> hostList = new ArrayList<String>(1);
        hostList.add(hostAndPortString);
        if (databaseId.isEmpty()) {
            databaseId = maker.next().after("databasename=").before(';').value();
        }
        final String normalizedUrl = maker.clear().before(";").value();
        return new DefaultDatabaseInfo("sqlserver", url, normalizedUrl, hostList, databaseId);
    }
}
