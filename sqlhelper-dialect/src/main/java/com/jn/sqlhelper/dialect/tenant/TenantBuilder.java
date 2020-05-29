package com.jn.sqlhelper.dialect.tenant;

import com.jn.langx.Builder;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Collects;
import com.jn.sqlhelper.common.ddl.model.internal.JdbcType;
import com.jn.sqlhelper.dialect.expression.ExpressionSymbol;

import java.util.List;

public class TenantBuilder implements Builder<Tenant> {

    private String catalog;
    private String schema;
    private String table;
    private String column;

    private JdbcType jdbcType;

    private ExpressionSymbol symbol;

    private boolean not;

    private List values = Collects.emptyArrayList();

    public TenantBuilder catalog(String catalog) {
        this.catalog = catalog;
        return this;
    }

    public TenantBuilder schema(String schema) {
        this.schema = schema;
        return this;
    }

    public TenantBuilder table(String table) {
        this.table = table;
        return this;
    }

    public TenantBuilder table(String catalog, String schema, String table) {
        return catalog(catalog).schema(schema).table(table);
    }

    public TenantBuilder column(@Nullable String column) {
        this.column = column;
        return this;
    }

    public TenantBuilder column(String catalog, String schema, String table, String column) {
        return table(catalog, schema, table).column(column);
    }

    public TenantBuilder not(boolean not) {
        this.not = not;
        return this;
    }

    public <T> TenantBuilder values(@Nullable T tenantValue) {
        this.values.add(tenantValue);
        return this;
    }

    public TenantBuilder jdbcType(JdbcType jdbcType) {
        this.jdbcType = jdbcType;
        return this;
    }

    public TenantBuilder symbol(ExpressionSymbol symbol) {
        this.symbol = symbol;
        return this;
    }

    public TenantBuilder values(@Nullable List<?> tenantValues) {
        if (Emptys.isNotEmpty(tenantValues)) {
            this.values = tenantValues;
        }
        return this;
    }

    public Tenant build() {
        Tenant tenant = new Tenant();
        tenant.setCatalog(catalog);
        tenant.setSchema(schema);
        tenant.setTable(table);
        tenant.setColumn(column);
        tenant.setNot(not);
        tenant.setSymbol(symbol);
        tenant.setJdbcType(jdbcType);
        tenant.setValues(values);
        return tenant;
    }

}
