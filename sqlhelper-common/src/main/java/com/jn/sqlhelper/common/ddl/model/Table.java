package com.jn.sqlhelper.common.ddl.model;

import com.jn.easyjson.core.JSONBuilderProvider;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Predicate;
import com.jn.sqlhelper.common.annotation.Column;
import com.jn.sqlhelper.common.ddl.model.internal.TableType;
import com.jn.sqlhelper.common.ddl.model.internal.TableTypeConverter;

import java.util.*;

public class Table {
    @Nullable
    @Column({"TABLE_CAT", "TABLE_CATALOG"})
    private String catalog;

    @Nullable
    @Column({"TABLE_SCHEM", "TABLE_SCHEMA"})
    private String schema;

    @NonNull
    @Column("TABLE_NAME")
    private String name;

    @NonNull
    @Column(value = "TABLE_TYPE", converter = TableTypeConverter.class)
    private TableType tableType;

    @Nullable
    private String remarks;

    @Column("TYPE_CAT")
    private String typeCatalog;

    @Column("TYPE_SCHEM")
    private String typeSchema;

    private String typeName;

    @Column("SELF_REFERENCING_COL_NAME")
    private String selfReferencingColumnName;

    private String refGeneration;

    @Nullable
    private String sql;

    private final Map<String, Index> indexMap = new TreeMap<String, Index>(new Comparator<String>() {
        @Override
        public int compare(String key1, String key2) {
            return key1.compareToIgnoreCase(key2);
        }
    });
    private final Set<com.jn.sqlhelper.common.ddl.model.Column> columns = new TreeSet<com.jn.sqlhelper.common.ddl.model.Column>(new Comparator<com.jn.sqlhelper.common.ddl.model.Column>() {
        @Override
        public int compare(com.jn.sqlhelper.common.ddl.model.Column o1, com.jn.sqlhelper.common.ddl.model.Column o2) {
            return o1.getOrdinalPosition() - o2.getOrdinalPosition();
        }
    });
    private final Set<PrimaryKeyColumn> pkColumns = new TreeSet<PrimaryKeyColumn>(new Comparator<PrimaryKeyColumn>() {
        @Override
        public int compare(PrimaryKeyColumn o1, PrimaryKeyColumn o2) {
            return o1.getKeySeq() - o2.getKeySeq();
        }
    });

    private final Map<String, ImportedColumn> fkColumnMap = new HashMap<String, ImportedColumn>();

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

    public Map<String, Index> getIndexMap() {
        return indexMap;
    }

    public Set<com.jn.sqlhelper.common.ddl.model.Column> getColumns() {
        return columns;
    }

    public void addIndex(Index index) {
        indexMap.put(index.getName(), index);
    }

    public Index getIndex(String indexName) {
        return indexMap.get(indexName);
    }

    public void addColumn(com.jn.sqlhelper.common.ddl.model.Column column) {
        columns.add(column);
    }

    public com.jn.sqlhelper.common.ddl.model.Column getColumn(final String columnName) {
        return Collects.findFirst(columns, new Predicate<com.jn.sqlhelper.common.ddl.model.Column>() {
            @Override
            public boolean test(com.jn.sqlhelper.common.ddl.model.Column column) {
                return column.getName().equals(columnName);
            }
        });
    }

    public Set<PrimaryKeyColumn> getPkColumns() {
        return pkColumns;
    }

    public void addPKColumn(PrimaryKeyColumn primaryKeyColumn) {
        pkColumns.add(primaryKeyColumn);
    }

    public Map<String, ImportedColumn> getFkColumnMap() {
        return fkColumnMap;
    }

    public void addFKColumn(ImportedColumn fkColumn) {
        fkColumnMap.put(fkColumn.getFkColumnName(), fkColumn);
    }

}
