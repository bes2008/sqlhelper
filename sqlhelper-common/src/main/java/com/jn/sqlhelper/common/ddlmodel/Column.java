package com.jn.sqlhelper.common.ddlmodel;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.sqlhelper.common.utils.BooleanFlag;

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
    @com.jn.sqlhelper.common.annotation.Column("COLUMN_NAME")
    private String name;

    @NonNull
    @com.jn.sqlhelper.common.annotation.Column("DATA_TYPE")
    private JdbcType jdbcType;

    @com.jn.sqlhelper.common.annotation.Column("TYPE_NAME")
    private String typeName;

    @com.jn.sqlhelper.common.annotation.Column("COLUMN_SIZE")
    private int size;

    @com.jn.sqlhelper.common.annotation.Column("BUFFER_LENGTH")
    private int bufferLength;

    @com.jn.sqlhelper.common.annotation.Column("DECIMAL_DIGITS")
    private int decimalDigits;

    @com.jn.sqlhelper.common.annotation.Column("NULLABLE")
    private int nullable;

    @com.jn.sqlhelper.common.annotation.Column("REMARKS")
    private String remarks;

    @com.jn.sqlhelper.common.annotation.Column("COLUMN_DEF")
    private String defaultValue;

    @com.jn.sqlhelper.common.annotation.Column("SQL_DATA_TYPE")
    private int sqlDataType;

    @com.jn.sqlhelper.common.annotation.Column("SQL_DATETIME_SUB")
    private int sqlDatetimeSub;

    @com.jn.sqlhelper.common.annotation.Column("CHAR_OCTET_LENGTH")
    private int charOctetLength;

    @com.jn.sqlhelper.common.annotation.Column("ORDINAL_POSITION")
    private int ordinalPosition;

    @com.jn.sqlhelper.common.annotation.Column("IS_NULLABLE")
    private BooleanFlag isNullable;

    @com.jn.sqlhelper.common.annotation.Column("SCOPE_CATALOG")
    private String scopeCatalog;

    @com.jn.sqlhelper.common.annotation.Column("SCOPE_SCHEMA")
    private String scopeSchema;

    @com.jn.sqlhelper.common.annotation.Column("SCOPE_TABLE")
    private String scopeTable;

    @com.jn.sqlhelper.common.annotation.Column("SOURCE_DATA_TYPE")
    private JdbcType sourceDataType;

    @com.jn.sqlhelper.common.annotation.Column("IS_AUTOINCREMENT")
    private BooleanFlag isAutoincrement;

    @com.jn.sqlhelper.common.annotation.Column("IS_GENERATEDCOLUMN")
    private BooleanFlag isGeneratedColumn;

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(JdbcType jdbcType) {
        this.jdbcType = jdbcType;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getBufferLength() {
        return bufferLength;
    }

    public void setBufferLength(int bufferLength) {
        this.bufferLength = bufferLength;
    }

    public int getDecimalDigits() {
        return decimalDigits;
    }

    public void setDecimalDigits(int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    public int getNullable() {
        return nullable;
    }

    public void setNullable(int nullable) {
        this.nullable = nullable;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public int getSqlDataType() {
        return sqlDataType;
    }

    public void setSqlDataType(int sqlDataType) {
        this.sqlDataType = sqlDataType;
    }

    public int getSqlDatetimeSub() {
        return sqlDatetimeSub;
    }

    public void setSqlDatetimeSub(int sqlDatetimeSub) {
        this.sqlDatetimeSub = sqlDatetimeSub;
    }

    public int getCharOctetLength() {
        return charOctetLength;
    }

    public void setCharOctetLength(int charOctetLength) {
        this.charOctetLength = charOctetLength;
    }

    public int getOrdinalPosition() {
        return ordinalPosition;
    }

    public void setOrdinalPosition(int ordinalPosition) {
        this.ordinalPosition = ordinalPosition;
    }

    public BooleanFlag getIsNullable() {
        return isNullable;
    }

    public void setIsNullable(BooleanFlag isNullable) {
        this.isNullable = isNullable;
    }

    public String getScopeCatalog() {
        return scopeCatalog;
    }

    public void setScopeCatalog(String scopeCatalog) {
        this.scopeCatalog = scopeCatalog;
    }

    public String getScopeSchema() {
        return scopeSchema;
    }

    public void setScopeSchema(String scopeSchema) {
        this.scopeSchema = scopeSchema;
    }

    public String getScopeTable() {
        return scopeTable;
    }

    public void setScopeTable(String scopeTable) {
        this.scopeTable = scopeTable;
    }

    public JdbcType getSourceDataType() {
        return sourceDataType;
    }

    public void setSourceDataType(JdbcType sourceDataType) {
        this.sourceDataType = sourceDataType;
    }

    public BooleanFlag getIsAutoincrement() {
        return isAutoincrement;
    }

    public void setIsAutoincrement(BooleanFlag isAutoincrement) {
        this.isAutoincrement = isAutoincrement;
    }

    public BooleanFlag getIsGeneratedColumn() {
        return isGeneratedColumn;
    }

    public void setIsGeneratedColumn(BooleanFlag isGeneratedColumn) {
        this.isGeneratedColumn = isGeneratedColumn;
    }
}
