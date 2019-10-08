package com.jn.sqlhelper.common.exception;

public class ValueConvertException extends RuntimeException {
    public ValueConvertException() {
        super();
    }

    public ValueConvertException(String message) {
        super(message);
    }

    public ValueConvertException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValueConvertException(Throwable cause) {
        super(cause);
    }

}
