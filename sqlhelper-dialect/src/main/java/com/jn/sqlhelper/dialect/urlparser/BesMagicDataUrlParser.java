package com.jn.sqlhelper.dialect.urlparser;

import java.util.Arrays;
import java.util.List;

public class BesMagicDataUrlParser extends CommonUrlParser {
    public static final int DEFAULT_PORT = 12345;
    private static final String URL_PREFIX = "jdbc:besmagicdata:";
    private static final List<String> URL_SCHEMAS = Arrays.asList(URL_PREFIX);

    @Override
    public List<String> getUrlSchemas() {
        return URL_SCHEMAS;
    }

    public BesMagicDataUrlParser() {

    }

}
