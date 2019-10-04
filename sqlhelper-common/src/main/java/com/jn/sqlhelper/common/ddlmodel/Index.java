package com.jn.sqlhelper.common.ddlmodel;

import com.jn.easyjson.core.JSONBuilderProvider;
import com.jn.sqlhelper.common.annotation.Column;
import com.jn.sqlhelper.common.utils.IndexType;
import com.jn.sqlhelper.common.utils.IndexTypeConverter;
import com.jn.sqlhelper.common.utils.SortType;
import com.jn.sqlhelper.common.utils.SortTypeConverter;

public class Index {
    @Column({"TABLE_CAT", "TABLE_CATALOG"})
    private String catalog;

    @Column({"TABLE_SCHEM", "TABLE_SCHEMA"})
    private String schema;

    @Column("TABLE_NAME")
    private String tableName;

    @Column("NON_UNIQUE")
    private boolean nonUnique;

    @Column("INDEX_QUALIFIER")
    private String indexQualifier;

    @Column("INDEX_NAME")
    private String name;

    @Column(value = {"TYPE", "INDEX_TYPE"}, converter = IndexTypeConverter.class)
    private IndexType type;

    @Column("ORDINAL_POSITION")
    private int ordinalPosition;

    @Column("COLUMN_NAME")
    private String columnName;

    @Column(value = {"ASC_OR_DESC"}, converter = SortTypeConverter.class)
    private SortType ascOrDesc;

    private int sortType;

    private long cardinality;

    private long pages;

    @Column("FILTER_CONDITION")
    private String filterCondition;

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

    public boolean isNonUnique() {
        return nonUnique;
    }

    public void setNonUnique(boolean nonUnique) {
        this.nonUnique = nonUnique;
    }

    public String getIndexQualifier() {
        return indexQualifier;
    }

    public void setIndexQualifier(String indexQualifier) {
        this.indexQualifier = indexQualifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IndexType getType() {
        return type;
    }

    public void setType(IndexType type) {
        this.type = type;
    }

    public int getOrdinalPosition() {
        return ordinalPosition;
    }

    public void setOrdinalPosition(int ordinalPosition) {
        this.ordinalPosition = ordinalPosition;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public SortType getAscOrDesc() {
        return ascOrDesc;
    }

    public void setAscOrDesc(SortType ascOrDesc) {
        this.ascOrDesc = ascOrDesc;
    }

    public long getCardinality() {
        return cardinality;
    }

    public void setCardinality(long cardinality) {
        this.cardinality = cardinality;
    }

    public long getPages() {
        return pages;
    }

    public void setPages(long pages) {
        this.pages = pages;
    }

    public String getFilterCondition() {
        return filterCondition;
    }

    public void setFilterCondition(String filterCondition) {
        this.filterCondition = filterCondition;
    }

    public int getSortType() {
        return sortType;
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
    }

    @Override
    public String toString() {
        return JSONBuilderProvider.create().prettyFormat(true).build().toJson(this);
    }
}
