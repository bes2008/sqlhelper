package com.jn.sqlhelper.common.ddlmodel;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;

import java.sql.Types;

public class Column {
    @Nullable
    @com.jn.sqlhelper.common.annotation.Column("TABLE_CAT")
    private String catalog;
    @Nullable
    @com.jn.sqlhelper.common.annotation.Column("TABLE_SCHEM")
    private String schema;
    @NonNull
    @com.jn.sqlhelper.common.annotation.Column("TABLE_NAME")
    private String tableName;

    @NonNull
    @com.jn.sqlhelper.common.annotation.Column("TABLE_NAME")
    private String name;

    private JdbcType jdbcType;
    private String typeName;

    private int size;

    private int bufferLength;

}
