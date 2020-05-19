package com.jn.sqlhelper.dialect.tenant;

import com.jn.langx.annotation.Nullable;

/**
 * Usage:
 * <pre>
 *  Tenant tenant = new ProgrameStyleTenantByBuilder()
 *      .tableFilter("timeline")
 *      .column("userXXX")
 *      .compore("in")
 *      .value("1")
 *      .build();
 *  </pre>
 */

public interface TenantBuilder<T> {

    TenantBuilder column(@Nullable String column);

    TenantBuilder value(@Nullable T tenantValue);

    Tenant build();

}
