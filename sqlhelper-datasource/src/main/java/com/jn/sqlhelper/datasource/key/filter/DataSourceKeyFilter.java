package com.jn.sqlhelper.datasource.key.filter;

import com.jn.langx.Named;
import com.jn.langx.Ordered;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.invocation.MethodInvocation;
import com.jn.langx.util.function.Function2;
import com.jn.sqlhelper.datasource.key.DataSourceKey;

import java.util.List;

public interface DataSourceKeyFilter extends Function2<List<DataSourceKey>, MethodInvocation, DataSourceKey>, Named, Ordered {
    /**
     * 可应用与哪些 group
     */
    @NonNull
    List<String> applyTo();
}
