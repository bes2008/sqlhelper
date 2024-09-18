package com.jn.sqlhelper.dialect.urlparser;

import java.util.ArrayList;
import java.util.List;

public class NoopUrlParser extends CommonUrlParser {
    @Override
    public DatabaseInfo parse(String url) {
        return UnKnownDatabaseInfo.INSTANCE;
    }

    @Override
    public List<String> getUrlSchemas() {
        return new ArrayList<String>();
    }
}
