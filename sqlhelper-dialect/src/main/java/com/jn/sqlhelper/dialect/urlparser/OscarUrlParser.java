package com.jn.sqlhelper.dialect.urlparser;

import java.util.Arrays;
import java.util.List;

public class OscarUrlParser extends CommonUrlParser {
    private static final String URL_PREFIX = "jdbc:oscar:";
    private static final List<String> URL_SCHEMAS = Arrays.asList(new String[]{URL_PREFIX});

    @Override
    public List<String> getUrlSchemas() {
        return URL_SCHEMAS;
    }

    public OscarUrlParser() {

    }

    @Override
    public String getName() {
        return "oscar";
    }
}
