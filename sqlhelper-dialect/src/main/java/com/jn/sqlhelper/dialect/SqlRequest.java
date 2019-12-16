package com.jn.sqlhelper.dialect;

import java.io.Serializable;

public class SqlRequest<R extends SqlRequest> implements Serializable {
    private static final long serialVersionUID = 1L;

    private String dialect;

    public String getDialect() {
        return dialect;
    }

    public SqlRequest<R> setDialect(String dialect) {
        this.dialect = dialect;
        return this;
    }
}
