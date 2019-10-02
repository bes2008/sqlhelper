package com.jn.sqlhelper.common.exception;

public class NoMappedFieldException extends RuntimeException {
    public NoMappedFieldException() {
        super();
    }

    public NoMappedFieldException(String message) {
        super(message);
    }

    public NoMappedFieldException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoMappedFieldException(Throwable cause) {
        super(cause);
    }

}
