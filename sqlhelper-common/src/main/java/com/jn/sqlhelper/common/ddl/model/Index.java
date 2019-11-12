package com.jn.sqlhelper.common.ddl.model;

import com.jn.easyjson.core.JSONBuilderProvider;
import com.jn.langx.util.Preconditions;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class Index {
    private String catalog;
    private String schema;
    private String tableName;
    private String name;

    private final Set<IndexColumn> indexColumns = new TreeSet<IndexColumn>(new Comparator<IndexColumn>() {
        @Override
        public int compare(IndexColumn c1, IndexColumn c2) {
            return c1.getOrdinalPosition() - c2.getOrdinalPosition();
        }
    });

    public Index() {
    }

    public Index(String table, String name) {
        this(null, null, table, name);
    }

    public Index(String catalog, String schema, String table, String name) {
        setCatalog(catalog);
        setSchema(schema);
        setTableName(table);
        setName(name);
    }

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

    public void setTableName(String table) {
        Preconditions.checkNotNull(table);
        this.tableName = table;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        Preconditions.checkNotNull(name);
        this.name = name;
    }

    public Set<IndexColumn> getColumns() {
        return indexColumns;
    }


    public void addColumn(IndexColumn indexColumn) {
        indexColumns.add(indexColumn);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Index index = (Index) o;

        if (catalog != null ? !catalog.equals(index.catalog) : index.catalog != null) {
            return false;
        }
        if (schema != null ? !schema.equals(index.schema) : index.schema != null) {
            return false;
        }
        if (!tableName.equals(index.tableName)) {
            return false;
        }
        return name.equals(index.name);
    }

    @Override
    public int hashCode() {
        int result = catalog != null ? catalog.hashCode() : 0;
        result = 31 * result + (schema != null ? schema.hashCode() : 0);
        result = 31 * result + tableName.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return JSONBuilderProvider.create().serializeNulls(true).prettyFormat(true).build().toJson(this);
    }
}
