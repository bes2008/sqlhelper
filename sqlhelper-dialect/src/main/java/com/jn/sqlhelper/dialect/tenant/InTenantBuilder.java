package com.jn.sqlhelper.dialect.tenant;

import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Preconditions;

import java.util.List;

public class InTenantBuilder implements TenantBuilder<List<String>> {
    public static final InTenantBuilder DEFAULT = new InTenantBuilder();
    private List<String> tenantValue;
    private String tenantColumn;
    public InTenantBuilder() {
    }
    public InTenantBuilder column(String column) {
        Preconditions.checkNotNull(column);
        if (tenantColumn == null) {
            tenantColumn = column ;
            return this;
        }
        if (!column.equalsIgnoreCase(this.tenantColumn)) {
            tenantColumn = null;
            column(column);
        }
        return this;
    }
    public TenantBuilder value(@Nullable List<String> tenantValue) {
        Preconditions.checkNotNull(tenantColumn, "you should set which tenantColumn to tenant first");
        if(tenantValue instanceof List){
            this.tenantValue=tenantValue;
        }else{
            throw new IllegalArgumentException("type in mush value list instanceof ");
        }
        return this;
    }
    public Tenant build() {
        Tenant tenant = new Tenant();
        tenant.setTenantColumn(this.tenantColumn);
        tenant.setMultipleTenant(true);
        tenant.setMultipleTenantValues(this.tenantValue);
        return tenant;
    }
}
