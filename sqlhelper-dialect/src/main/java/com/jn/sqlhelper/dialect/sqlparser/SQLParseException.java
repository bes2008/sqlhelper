package com.jn.sqlhelper.dialect.sqlparser;

import com.jn.sqlhelper.dialect.SQLDialectException;

public class SQLParseException extends SQLDialectException {
    public SQLParseException() {
        super();
    }

    public SQLParseException(String message) {
        super(message);
    }

    public SQLParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public SQLParseException(Throwable cause) {
        super(cause);
    }
}
