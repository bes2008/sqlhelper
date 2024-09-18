package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.urlparser.MariaDBUrlParser;

public class MariaDBDialect extends MySQLDialect {
    public MariaDBDialect() {
        super();
        setUrlParser(new MariaDBUrlParser());
    }
}
