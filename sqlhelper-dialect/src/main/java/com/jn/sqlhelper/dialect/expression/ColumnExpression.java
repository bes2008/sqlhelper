package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.el.expression.BaseExpression;
import com.jn.langx.util.hash.HashCodeBuilder;
import com.jn.sqlhelper.common.utils.SQLs;

public class ColumnExpression extends BaseExpression<SQLExpression> implements SQLExpression<SQLExpression> {
    private boolean catalogAtStart = true;
    private String catalog;
    private String schema;
    private String table;
    private String column;
    private String separator = ".";

    public ColumnExpression(){
    }

    public ColumnExpression(String column){
        this(null,column);
    }
    public ColumnExpression(String table, String column){
        this(null, null, table, column);
    }

    public ColumnExpression(String catalog, String schema, String table, String column){
        this(catalog, schema, table, column, ".", true);
    }

    public ColumnExpression(String catalog, String schema, String table, String column, String separator, boolean catalogAtStart) {
        setCatalog(catalog);
        setSchema(schema);
        setTable(table);
        setColumn(column);
        setCatalogAtStart(catalogAtStart);
        setSeparator(separator);
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
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

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public boolean isCatalogAtStart() {
        return catalogAtStart;
    }

    public void setCatalogAtStart(boolean catalogAtStart) {
        this.catalogAtStart = catalogAtStart;
    }

    @Override
    public SQLExpression execute() {
        return this;
    }


    public String getTableFullyQualifiedName(){
        return SQLs.getTableFQN(catalog, schema, table, separator, catalogAtStart);
    }

    @Override
    public String toString() {
        return getTableFullyQualifiedName() + separator + column;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().with(catalogAtStart)
                .with(separator)
                .with(catalog)
                .with(schema)
                .with(table)
                .with(column)
                .build();
    }
}
