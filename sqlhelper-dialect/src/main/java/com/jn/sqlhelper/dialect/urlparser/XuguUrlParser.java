package com.jn.sqlhelper.dialect.urlparser;

// com.xugu.cloudjdbc.Driver
// 虚谷数据库 http://www.xugucn.com/Single_index_id_30.shtml


import com.jn.langx.util.collection.Collects;

import java.util.List;

/**
 * jdbc:xugu://serverIP:portNumber/databaseName[?property=value[&property=value]]
 */
public class XuguUrlParser extends CommonUrlParser {
    private static final String URL_PREFIX = "jdbc:xugu:";
    private static final List<String> URL_SCHEMAS = Collects.newArrayList(URL_PREFIX);

    @Override
    public List<String> getUrlSchemas() {
        return URL_SCHEMAS;
    }
}
