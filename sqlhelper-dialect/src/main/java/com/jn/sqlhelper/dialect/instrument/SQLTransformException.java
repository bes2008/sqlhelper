package com.jn.sqlhelper.dialect.instrument;

import com.jn.sqlhelper.dialect.SQLDialectException;

public class SQLTransformException extends SQLDialectException {
    public SQLTransformException(String message) {
        super(message);
    }
    public SQLTransformException(Throwable ex) {
        super(ex);
    }
}
