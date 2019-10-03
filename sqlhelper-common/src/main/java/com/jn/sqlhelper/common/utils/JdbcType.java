package com.jn.sqlhelper.common.utils;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Predicate;

import java.sql.Types;
import java.util.EnumSet;

public enum JdbcType {
    BIT(Types.BIT),
    TINYINT(Types.TINYINT),
    SMALLINT(Types.SMALLINT),
    INTEGER(Types.INTEGER),
    BIGINT(Types.BIGINT),
    FLOAT(Types.FLOAT),
    REAL(Types.REAL),
    DOUBLE(Types.DOUBLE),
    NUMERIC(Types.NUMERIC),
    DECIMAL(Types.DECIMAL),
    CHAR(Types.CHAR),
    VARCHAR(Types.VARCHAR),
    LONGVARCHAR(Types.LONGVARCHAR),
    DATE(Types.DATE),
    TIME(Types.TIME),
    TIMESTAMP(Types.TIMESTAMP),
    BINARY(Types.BINARY),
    VARBINARY(Types.VARBINARY),
    LONGVARBINARY(Types.LONGVARBINARY),
    NULL(Types.NULL),
    OTHER(Types.OTHER),
    JAVA_OBJECT(Types.JAVA_OBJECT),
    DISTINCT(Types.DISTINCT),
    STRUCT(Types.STRUCT),
    ARRAY(Types.ARRAY),
    BLOB(Types.BLOB),
    CLOB(Types.CLOB),
    REF(Types.REF),
    DATALINK(Types.DATALINK),
    BOOLEAN(Types.BOOLEAN),
    ROWID(Types.ROWID),
    NCHAR(Types.NCHAR),
    NVARCHAR(Types.NVARCHAR),
    LONGNVARCHAR(Types.LONGNVARCHAR),
    NCLOB(Types.NCLOB),
    SQLXML(Types.SQLXML),
    REF_CURSOR(2012),
    TIME_WITH_TIMEZONE(2013),
    TIMESTAMP_WITH_TIMEZONE(2014),


    UNKNOWN(Integer.MIN_VALUE);

    JdbcType() {
    }

    JdbcType(int code) {
        this.code = code;
    }


    private int code;

    public static final JdbcType ofCode(final int code) {
        JdbcType jdbcType = Collects.findFirst(EnumSet.allOf(JdbcType.class), new Predicate<JdbcType>() {
            @Override
            public boolean test(JdbcType jdbcType) {
                return jdbcType.code == code;
            }
        });
        return jdbcType == null ? UNKNOWN : jdbcType;
    }

}
