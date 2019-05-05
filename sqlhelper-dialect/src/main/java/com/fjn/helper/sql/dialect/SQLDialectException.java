package com.fjn.helper.sql.dialect;

public class SQLDialectException extends RuntimeException {
    public SQLDialectException(final String message) {

        super(message);
    }

    public SQLDialectException(final Throwable cause) {

        super(cause);
    }

    public SQLDialectException(final String message, final Throwable cause) {

        super(message, cause);
    }
}
