package com.fjn.helper.sql.dialect.internal.urlparser;

import com.fjn.helper.sql.dialect.DatabaseInfo;

import java.util.List;

public interface UrlParser {
    DatabaseInfo parse(final String url);

    List<String> getUrlSchemas();
}
