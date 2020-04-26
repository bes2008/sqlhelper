package com.jn.sqlhelper.dialect.tenant;

import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Preconditions;

import java.util.Comparator;
import java.util.List;

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

public interface TenantByBuilder<T> {

     TenantByBuilder column(@Nullable String column);

     TenantByBuilder value(@Nullable T tenantValue) ;

     Tenant build() ;

}
