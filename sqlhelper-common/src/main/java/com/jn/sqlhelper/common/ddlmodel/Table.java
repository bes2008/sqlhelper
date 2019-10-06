package com.jn.sqlhelper.common.ddlmodel;

import com.jn.easyjson.core.JSONBuilderProvider;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.io.LineDelimiter;
import com.jn.sqlhelper.common.annotation.Column;
import com.jn.sqlhelper.common.utils.TableType;
import com.jn.sqlhelper.common.utils.TableTypeConverter;
import com.jn.sqlhelper.common.utils.Utils;

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
    private final Set<com.jn.sqlhelper.common.ddlmodel.Column> columns = new TreeSet<com.jn.sqlhelper.common.ddlmodel.Column>(new Comparator<com.jn.sqlhelper.common.ddlmodel.Column>() {
        @Override
        public int compare(com.jn.sqlhelper.common.ddlmodel.Column o1, com.jn.sqlhelper.common.ddlmodel.Column o2) {
            return o1.getOrdinalPosition() - o2.getOrdinalPosition();
        }
    });

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

    public Set<com.jn.sqlhelper.common.ddlmodel.Column> getColumns() {
        return columns;
    }

    public void addIndex(Index index) {
        indexMap.put(index.getName(), index);
    }

    public Index getIndex(String indexName) {
        return indexMap.get(indexName);
    }

    public void addColumn(com.jn.sqlhelper.common.ddlmodel.Column column) {
        columns.add(column);
    }

    public com.jn.sqlhelper.common.ddlmodel.Column getColumn(final String columnName) {
        return Collects.findFirst(columns, new Predicate<com.jn.sqlhelper.common.ddlmodel.Column>() {
            @Override
            public boolean test(com.jn.sqlhelper.common.ddlmodel.Column column) {
                return column.getName().equals(columnName);
            }
        });
    }

    public String showAsDDL() {
        return showAsDDL(true);
    }

    public String showAsDDL(boolean showIndexes) {
        if (tableType == TableType.SYSTEM_TABLE || tableType == TableType.TABLE || tableType == TableType.GLOBAL_TEMPORARY || tableType == TableType.LOCAL_TEMPORARY) {
            return showAsTableDDL(showIndexes);
        } else {
            final String lineDelimiter = LineDelimiter.DEFAULT.getValue();
            final StringBuilder builder = new StringBuilder(256);

            if (Strings.isNotEmpty(sql)) {
                builder.append(sql);
                if (!sql.endsWith(";")) {
                    builder.append(";");
                }
                builder.append(lineDelimiter);
            } else {
                if (tableType == TableType.ALIAS) {
                    builder.append("CREATE ALIAS ");
                } else if (tableType == TableType.SYNONYM) {
                    builder.append("CREATE SYNONYM ");
                } else {
                    builder.append("CREATE VIEW ");
                }
                builder.append(name).append(";").append(lineDelimiter);
            }
            return builder.toString();
        }
    }

    private String showAsTableDDL(boolean showIndexes) {
        final String lineDelimiter = LineDelimiter.DEFAULT.getValue();
        final StringBuilder builder = new StringBuilder(256);
        if (Strings.isEmpty(sql)) {
            builder.append("CREATE");
            if (tableType == TableType.GLOBAL_TEMPORARY || tableType == TableType.LOCAL_TEMPORARY) {
                builder.append(" ").append(tableType.getCode());
            }
            builder.append(" TABLE ").append(name).append(lineDelimiter);

            builder.append("(").append(lineDelimiter);
            Utils.forEach(columns, new Consumer2<Integer, com.jn.sqlhelper.common.ddlmodel.Column>() {
                @Override
                public void accept(Integer i, com.jn.sqlhelper.common.ddlmodel.Column column) {
                    if (i > 0) {
                        builder.append(",").append(lineDelimiter);
                    }
                    builder.append("\t").append(column.showAsDDLColumn());
                }
            });
            builder.append(lineDelimiter);

            builder.append(");").append(lineDelimiter);
        } else {
            builder.append(sql);
            if (!sql.endsWith(";")) {
                builder.append(";");
            }
            builder.append(lineDelimiter);
        }
        if (showIndexes) {
            Collects.forEach(indexMap, new Consumer2<String, Index>() {
                @Override
                public void accept(String key, Index index) {
                    builder.append(index.showAsDDL());
                }
            });
        }
        return builder.toString();
    }


}
