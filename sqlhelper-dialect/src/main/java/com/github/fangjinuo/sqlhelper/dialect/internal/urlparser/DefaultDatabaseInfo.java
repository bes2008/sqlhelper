
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

import java.util.List;

public class DefaultDatabaseInfo implements DatabaseInfo {
    private final String databaseId;
    private final String realUrl;
    private final String normalizedUrl;
    private final List<String> host;
    private final String multipleHost;
    private final String verdor;
    private final boolean parsingComplete;

    public DefaultDatabaseInfo(final String verdor, final String realUrl, final String normalizedUrl, final List<String> host, final String databaseId) {
        this(verdor, realUrl, normalizedUrl, host, databaseId, true);
    }

    public DefaultDatabaseInfo(final String verdor, final String realUrl, final String normalizedUrl, final List<String> host, final String databaseId, final boolean parsingComplete) {
        this.verdor = verdor;
        this.realUrl = realUrl;
        this.normalizedUrl = normalizedUrl;
        this.host = host;
        this.multipleHost = this.merge(host);
        this.databaseId = databaseId;
        this.parsingComplete = parsingComplete;
    }

    private String merge(final List<String> host) {
        if (host.isEmpty()) {
            return "";
        }
        final String single = (String) host.get(0);
        final StringBuilder sb = new StringBuilder();
        sb.append(single);
        for (int i = 1; i < host.size(); ++i) {
            sb.append(',');
            sb.append(host.get(i));
        }
        return sb.toString();
    }

    @Override
    public List<String> getHost() {
        return this.host;
    }

    @Override
    public String getMultipleHost() {
        return this.multipleHost;
    }

    @Override
    public String getDatabaseId() {
        return this.databaseId;
    }

    @Override
    public String getRealUrl() {
        return this.realUrl;
    }

    @Override
    public String getUrl() {
        return this.normalizedUrl;
    }

    @Override
    public boolean isParsingComplete() {
        return this.parsingComplete;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DefaultDatabaseInfo{");
        sb.append("verdor=").append(this.verdor);
        sb.append(", databaseId='").append(this.databaseId).append('\'');
        sb.append(", realUrl='").append(this.realUrl).append('\'');
        sb.append(", normalizedUrl='").append(this.normalizedUrl).append('\'');
        sb.append(", host=").append(this.host);
        sb.append(", multipleHost='").append(this.multipleHost).append('\'');
        sb.append(", parsingComplete=").append(this.parsingComplete);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public String getVendor() {
        return this.verdor;
    }
}
