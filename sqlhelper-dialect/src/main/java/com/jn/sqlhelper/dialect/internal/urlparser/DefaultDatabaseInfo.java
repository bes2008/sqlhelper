package com.jn.sqlhelper.dialect.internal.urlparser;

import com.jn.langx.util.Objs;
import com.jn.sqlhelper.dialect.urlparser.DatabaseInfo;

import java.util.List;


public class DefaultDatabaseInfo implements DatabaseInfo {
    private final String databaseInstance;
    private final String realUrl;
    private final String normalizedUrl;
    private final List<String> host;
    private final String multipleHost;
    private final String vendor;
    private final boolean parsingComplete;

    public DefaultDatabaseInfo(final String vendor, final String realUrl, final String normalizedUrl, final List<String> host, final String databaseInstance) {
        this(vendor, realUrl, normalizedUrl, host, databaseInstance, true);
    }

    public DefaultDatabaseInfo(final String vendor, final String realUrl, final String normalizedUrl, final List<String> host, final String databaseInstance, final boolean parsingComplete) {
        this.vendor = Objs.useValueIfEmpty(vendor, UNKNOWN);
        this.realUrl = realUrl;
        this.normalizedUrl = normalizedUrl;
        this.host = host;
        this.multipleHost = this.merge(host);
        this.databaseInstance = databaseInstance;
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
    public String getDatabaseInstance() {
        return this.databaseInstance;
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
        sb.append("verdor=").append(this.vendor);
        sb.append(", databaseId='").append(this.databaseInstance).append('\'');
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
        return this.vendor;
    }
}
