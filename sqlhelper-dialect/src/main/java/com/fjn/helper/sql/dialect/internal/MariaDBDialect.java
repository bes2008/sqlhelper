package com.fjn.helper.sql.dialect.internal;

import com.fjn.helper.sql.dialect.internal.urlparser.MariaDBUrlParser;

public class MariaDBDialect extends MySQLDialect {
    public MariaDBDialect() {
        super();
        setUrlParser(new MariaDBUrlParser());
    }
}
