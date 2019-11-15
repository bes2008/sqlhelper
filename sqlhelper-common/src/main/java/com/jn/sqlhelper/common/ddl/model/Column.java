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
    private Integer size;

    private Integer bufferLength;

    private Integer decimalDigits;

    private Integer numPrecRadix;

    private Integer nullable;

    private String remarks;

    @com.jn.sqlhelper.common.annotation.Column("COLUMN_DEF")
    private String defaultValue;

    @com.jn.sqlhelper.common.annotation.Column(value = "SQL_DATA_TYPE", converter = JdbcTypeConverter.class)
    private JdbcType sqlDataType;

    private Integer sqlDatetimeSub;

    private Integer charOctetLength;

    private Integer ordinalPosition;

    @com.jn.sqlhelper.common.annotation.Column(value = "IS_NULLABLE", converter = BooleanFlagConverter.class)
    private BooleanFlag isNullable;

    private String scopeCatalog;

    private String scopeSchema;

    private String scopeTable;

    private Integer sourceDataType;

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

    public int getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getBufferLength() {
        return bufferLength;
    }

    public void setBufferLength(Integer bufferLength) {
        this.bufferLength = bufferLength;
    }

    public Integer getDecimalDigits() {
        return decimalDigits;
    }

    public void setDecimalDigits(Integer decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    public Integer getNumPrecRadix() {
        return numPrecRadix;
    }

    public void setNumPrecRadix(Integer numPrecRadix) {
        this.numPrecRadix = numPrecRadix;
    }

    public Integer getNullable() {
        return nullable;
    }

    public void setNullable(Integer nullable) {
        this.nullable = nullable;
    }

    public Integer getSqlDatetimeSub() {
        return sqlDatetimeSub;
    }

    public void setSqlDatetimeSub(Integer sqlDatetimeSub) {
        this.sqlDatetimeSub = sqlDatetimeSub;
    }

    public Integer getCharOctetLength() {
        return charOctetLength;
    }

    public void setCharOctetLength(Integer charOctetLength) {
        this.charOctetLength = charOctetLength;
    }

    public Integer getOrdinalPosition() {
        return ordinalPosition;
    }

    public void setOrdinalPosition(Integer ordinalPosition) {
        this.ordinalPosition = ordinalPosition;
    }

    public Integer getSourceDataType() {
        return sourceDataType;
    }

    public void setSourceDataType(Integer sourceDataType) {
        this.sourceDataType = sourceDataType;
    }

    @Override
    public String toString() {
        return JSONBuilderProvider.create().prettyFormat(true).build().toJson(this);
    }
}
