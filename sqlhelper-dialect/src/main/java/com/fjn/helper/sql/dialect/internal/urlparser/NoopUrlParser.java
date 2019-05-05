package com.fjn.helper.sql.dialect.internal.urlparser;

import com.fjn.helper.sql.dialect.DatabaseInfo;

import java.util.ArrayList;
import java.util.List;

public class NoopUrlParser implements UrlParser {
    @Override
    public DatabaseInfo parse(String url) {
        return UnKnownDatabaseInfo.INSTANCE;
    }

    @Override
    public List<String> getUrlSchemas() {
        return new ArrayList<>();
    }
}
