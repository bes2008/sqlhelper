package com.jn.sqlhelper.common.ddlmodel;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.io.LineDelimiter;
import com.jn.langx.util.struct.Holder;
import com.jn.sqlhelper.common.utils.SortType;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class Index {
    private String catalog;
    private String schema;
    private String table;
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
        setTable(table);
        setName(name);
    }

    private Set<IndexColumn> columns;

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

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        Preconditions.checkNotNull(table);
        this.table = table;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        Preconditions.checkNotNull(name);
        this.name = name;
    }

    public Set<IndexColumn> getColumns() {
        return columns;
    }


    public void addColumn(IndexColumn indexColumn) {
        indexColumns.add(indexColumn);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Index index = (Index) o;

        if (catalog != null ? !catalog.equals(index.catalog) : index.catalog != null) return false;
        if (schema != null ? !schema.equals(index.schema) : index.schema != null) return false;
        if (!table.equals(index.table)) return false;
        return name.equals(index.name);
    }

    @Override
    public int hashCode() {
        int result = catalog != null ? catalog.hashCode() : 0;
        result = 31 * result + (schema != null ? schema.hashCode() : 0);
        result = 31 * result + table.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    public String showAsDDL() {
        final StringBuilder builder = new StringBuilder(256);
        builder.append("CREATE INDEX ").append(name).append(" ON ").append(table).append(" (");
        forEach(indexColumns, new Consumer2<Integer, IndexColumn>() {
            @Override
            public void accept(Integer i, IndexColumn indexColumn) {
                if (i > 0) {
                    builder.append(", ");
                }

                builder.append(indexColumn.getColumnName());
                if (indexColumn.getAscOrDesc() != SortType.UNSUPPORTED) {
                    builder.append(" ").append(indexColumn.getAscOrDesc().name());
                }
            }
        });
        builder.append(");").append(LineDelimiter.DEFAULT.getValue());
        return builder.toString();
    }

    /**
     * Iterate every element
     */
    public static <E> void forEach(@Nullable Collection<E> collection, @NonNull final Consumer2<Integer, E> consumer) {
        Preconditions.checkNotNull(consumer);
        if (Emptys.isNotEmpty(collection)) {
            final Holder<Integer> indexHolder = new Holder<Integer>(-1);
            Collects.forEach(collection, new Consumer<E>() {
                @Override
                public void accept(E e) {
                    indexHolder.set(indexHolder.get() + 1);
                    consumer.accept(indexHolder.get(), e);
                }
            });
        }
    }

}
