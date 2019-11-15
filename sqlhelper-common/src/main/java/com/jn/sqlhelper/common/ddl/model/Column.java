package com.jn.sqlhelper.common.ddl.model;

import com.jn.easyjson.core.JSONBuilderProvider;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.sqlhelper.common.ddl.model.internal.BooleanFlag;
import com.jn.sqlhelper.common.ddl.model.internal.BooleanFlagConverter;
import com.jn.sqlhelper.common.ddl.model.internal.JdbcType;
import com.jn.sqlhelper.common.ddl.model.internal.JdbcTypeConverter;

public class Column {
    @Nullable
    @com.jn.sqlhelper.common.annotation.Column("TABLE_CAT")
    private String catalog;

    @Nullable
    @com.jn.sqlhelper.common.annotation.Column("TABLE_SCHEM")
    private String schema;

    @NonNull
    private String tableName;

    @NonNull
    @com.jn.sqlhelper.common.annotation.Column("COLUMN_NAME")
    private String name;

    @NonNull
    @com.jn.sqlhelper.common.annotation.Column(value = "DATA_TYPE", converter = JdbcTypeConverter.class)
    private JdbcType jdbcType;

    private String typeName;

    @com.jn.sqlhelper.common.annotation.Column("COLUMN_SIZE")
    private int size;

    private int bufferLength;

    private int decimalDigits;

    private int numPrecRadix;

    private int nullable;

    private String remarks;

    @com.jn.sqlhelper.common.annotation.Column("COLUMN_DEF")
    private String defaultValue;

    @com.jn.sqlhelper.common.annotation.Column(value = "SQL_DATA_TYPE", converter = JdbcTypeConverter.class)
    private JdbcType sqlDataType;

    private int sqlDatetimeSub;

    private int charOctetLength;

    private int ordinalPosition;

    @com.jn.sqlhelper.common.annotation.Column(value = "IS_NULLABLE", converter = BooleanFlagConverter.class)
    private BooleanFlag isNullable;

    private String scopeCatalog;

    private String scopeSchema;

    private String scopeTable;

    private int sourceDataType;

    @com.jn.sqlhelper.common.annotation.Column(value = "IS_AUTOINCREMENT", converter = BooleanFlagConverter.class)
    private BooleanFlag isAutoincrement;

    @com.jn.sqlhelper.common.annotation.Column(value = "IS_GENERATEDCOLUMN", converter = BooleanFlagConverter.class)
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

    public JdbcType getSqlDataType() {
        return sqlDataType;
    }

    public void setSqlDataType(JdbcType sqlDataType) {
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

    public Integer getSourceDataType() {
        return sourceDataType;
    }

    public void setSourceDataType(Integer sourceDataType) {
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

    public int getNumPrecRadix() {
        return numPrecRadix;
    }

    public void setNumPrecRadix(int numPrecRadix) {
        this.numPrecRadix = numPrecRadix;
    }

    @Override
    public String toString() {
        return JSONBuilderProvider.create().prettyFormat(true).build().toJson(this);
    }
}
