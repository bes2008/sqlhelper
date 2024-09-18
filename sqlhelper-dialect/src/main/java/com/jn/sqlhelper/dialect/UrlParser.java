package com.jn.sqlhelper.dialect;

import com.jn.sqlhelper.dialect.urlparser.DatabaseInfo;

import java.util.List;

public interface UrlParser {
    DatabaseInfo parse(final String url);

    List<String> getUrlSchemas();
}
