package com.jn.sqlhelper.common.ddlmodel;

import com.jn.easyjson.core.JSONBuilderProvider;
import com.jn.sqlhelper.common.annotation.Column;
import com.jn.sqlhelper.common.utils.TableType;
import com.jn.sqlhelper.common.utils.TableTypeConverter;

public class Table {
    @Column("TABLE_CAT")
    private String catalog;

    @Column("TABLE_SCHEM")
    private String schema;

    @Column("TABLE_NAME")
    private String name;

    @Column(value = "TABLE_TYPE", converter = TableTypeConverter.class)
    private TableType tableType;

    private String remarks;

    @Column("TYPE_CAT")
    private String typeCatalog;

    @Column("TYPE_SCHEM")
    private String typeSchema;

    private String typeName;

    @Column("SELF_REFERENCING_COL_NAME")
    private String selfReferencingColumnName;

    private String refGeneration;

    private String sql;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TableType getTableType() {
        return tableType;
    }

    public void setTableType(TableType tableType) {
        this.tableType = tableType;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getTypeCatalog() {
        return typeCatalog;
    }

    public void setTypeCatalog(String typeCatalog) {
        this.typeCatalog = typeCatalog;
    }

    public String getTypeSchema() {
        return typeSchema;
    }

    public void setTypeSchema(String typeSchema) {
        this.typeSchema = typeSchema;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getSelfReferencingColumnName() {
        return selfReferencingColumnName;
    }

    public void setSelfReferencingColumnName(String selfReferencingColumnName) {
        this.selfReferencingColumnName = selfReferencingColumnName;
    }

    public String getRefGeneration() {
        return refGeneration;
    }

    public void setRefGeneration(String refGeneration) {
        this.refGeneration = refGeneration;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    @Override
    public String toString() {
        return JSONBuilderProvider.create().prettyFormat(true).build().toJson(this);
    }
}
