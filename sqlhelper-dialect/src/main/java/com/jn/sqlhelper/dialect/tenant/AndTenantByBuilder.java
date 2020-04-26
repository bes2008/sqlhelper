package com.jn.sqlhelper.dialect.tenant;

import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Preconditions;


public class AndTenantByBuilder implements TenantByBuilder<String> {
    public static final AndTenantByBuilder DEFAULT = new AndTenantByBuilder();
    private String tenantValue;
    private String tenantColumn;

    public AndTenantByBuilder() {
    }
    public AndTenantByBuilder column(String column) {
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

    public TenantByBuilder value(@Nullable String tenantValue) {
        this.tenantValue=tenantValue;
        return this;
    }
    public Tenant build() {
        Tenant tenant = new Tenant();
        tenant.setTenantColumn(this.tenantColumn);
        tenant.setMultipleTenant(false);
        tenant.setSingleTenantValues(this.tenantValue);
        return tenant;
    }
}
